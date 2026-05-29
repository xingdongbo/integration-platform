package com.company.integration.exchange;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.company.integration.api.ApiInterface;
import com.company.integration.api.ApiInterfaceService;
import com.company.integration.common.BusinessException;
import com.company.integration.common.StatusConstants;
import com.company.integration.executor.ApiExecutor;
import com.company.integration.executor.TargetApiResult;
import com.company.integration.header.ApiHeader;
import com.company.integration.header.ApiHeaderService;
import com.company.integration.log.ApiExchangeLog;
import com.company.integration.log.ApiExchangeLogService;
import com.company.integration.mapping.ApiMappingRequest;
import com.company.integration.mapping.ApiMappingRequestService;
import com.company.integration.mapping.ApiMappingResponse;
import com.company.integration.mapping.ApiMappingResponseService;
import com.company.integration.mapping.MappingEngine;
import com.company.integration.retry.ApiRetryTask;
import com.company.integration.retry.ApiRetryTaskService;
import com.company.integration.system.SysSystem;
import com.company.integration.system.SysSystemService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ExchangeService {
    private final ApiInterfaceService interfaceService;
    private final SysSystemService systemService;
    private final ApiHeaderService headerService;
    private final ApiMappingRequestService requestMappingService;
    private final ApiMappingResponseService responseMappingService;
    private final ApiExchangeLogService logService;
    private final ApiRetryTaskService retryTaskService;
    private final MappingEngine mappingEngine;
    private final ApiExecutor apiExecutor;
    private final ObjectMapper objectMapper;

    public ExchangeService(ApiInterfaceService interfaceService,
                           SysSystemService systemService,
                           ApiHeaderService headerService,
                           ApiMappingRequestService requestMappingService,
                           ApiMappingResponseService responseMappingService,
                           ApiExchangeLogService logService,
                           ApiRetryTaskService retryTaskService,
                           MappingEngine mappingEngine,
                           ApiExecutor apiExecutor,
                           ObjectMapper objectMapper) {
        this.interfaceService = interfaceService;
        this.systemService = systemService;
        this.headerService = headerService;
        this.requestMappingService = requestMappingService;
        this.responseMappingService = responseMappingService;
        this.logService = logService;
        this.retryTaskService = retryTaskService;
        this.mappingEngine = mappingEngine;
        this.apiExecutor = apiExecutor;
        this.objectMapper = objectMapper;
    }

    public ExchangeResult exchange(String interfaceCode, Map<String, Object> sourceRequest) {
        return exchange(interfaceCode, sourceRequest, null);
    }

    public ExchangeResult exchange(String interfaceCode, Map<String, Object> sourceRequest, String existingTraceId) {
        String traceId = existingTraceId == null || existingTraceId.isBlank()
                ? UUID.randomUUID().toString().replace("-", "")
                : existingTraceId;
        long start = System.currentTimeMillis();
        ApiExchangeLog log = createInitialLog(traceId, interfaceCode, sourceRequest);
        try {
            ApiInterface apiInterface = findEnabledInterface(interfaceCode);
            SysSystem targetSystem = findEnabledSystem(apiInterface.targetSystem);
            log.sourceSystem = apiInterface.sourceSystem;
            log.targetSystem = apiInterface.targetSystem;
            logService.updateById(log);

            List<ApiMappingRequest> requestRules = requestMappingService.list(new QueryWrapper<ApiMappingRequest>()
                    .eq("interface_code", interfaceCode)
                    .orderByAsc("sort"));
            Map<String, Object> targetRequest = requestRules.isEmpty() ? sourceRequest : mappingEngine.mapRequest(sourceRequest, requestRules);
            updateLog(log, StatusConstants.MAPPED, targetRequest, null, null, null, start);

            String targetUrl = buildTargetUrl(targetSystem.baseUrl, apiInterface.targetUrl);
            List<ApiHeader> headers = headerService.list(new QueryWrapper<ApiHeader>().eq("interface_code", interfaceCode));
            updateLog(log, StatusConstants.CALLING, targetRequest, null, null, null, start);
            TargetApiResult targetResult = apiExecutor.execute(apiInterface, targetUrl, headers, targetRequest);
            if (!targetResult.successful) {
                throw new BusinessException("target api returned HTTP " + targetResult.statusCode + ": " + targetResult.rawBody);
            }

            List<ApiMappingResponse> responseRules = responseMappingService.list(new QueryWrapper<ApiMappingResponse>()
                    .eq("interface_code", interfaceCode)
                    .orderByAsc("sort"));
            Map<String, Object> sourceResponse = responseRules.isEmpty() ? targetResult.body : mappingEngine.mapResponse(targetResult.body, responseRules);
            updateLog(log, StatusConstants.SUCCESS, targetRequest, targetResult.rawBody, sourceResponse, null, start);
            return new ExchangeResult(traceId, targetRequest, targetResult.rawBody, sourceResponse);
        } catch (Exception ex) {
            updateLog(log, StatusConstants.FAILED, null, log.targetResponseRaw, null, ex.getMessage(), start);
            upsertRetryTask(traceId, interfaceCode, ex.getMessage());
            throw ex instanceof BusinessException ? (BusinessException) ex : new BusinessException("exchange failed: " + ex.getMessage(), ex);
        }
    }

    public ExchangeResult retry(String traceId) {
        ApiExchangeLog oldLog = logService.getOne(new QueryWrapper<ApiExchangeLog>().eq("trace_id", traceId), false);
        if (oldLog == null) {
            throw new BusinessException("exchange log not found: " + traceId);
        }
        ApiRetryTask retryTask = retryTaskService.getOne(new QueryWrapper<ApiRetryTask>().eq("trace_id", traceId), false);
        if (retryTask != null && retryTask.retryCount != null && retryTask.maxRetryCount != null && retryTask.retryCount >= retryTask.maxRetryCount) {
            throw new BusinessException("retry limit exceeded for traceId: " + traceId);
        }
        if (retryTask != null) {
            retryTask.status = StatusConstants.RETRYING;
            retryTask.retryCount = retryTask.retryCount == null ? 1 : retryTask.retryCount + 1;
            retryTaskService.updateById(retryTask);
        }
        try {
            Map<String, Object> sourceRequest = objectMapper.readValue(oldLog.sourceRequestRaw, new TypeReference<>() {});
            ExchangeResult result = exchange(oldLog.interfaceCode, sourceRequest, traceId + "_retry_" + System.currentTimeMillis());
            if (retryTask != null) {
                retryTask.status = StatusConstants.RETRY_SUCCESS;
                retryTask.lastErrorMsg = null;
                retryTaskService.updateById(retryTask);
            }
            return result;
        } catch (Exception ex) {
            if (retryTask != null) {
                retryTask.status = StatusConstants.RETRY_FAILED;
                retryTask.lastErrorMsg = ex.getMessage();
                retryTaskService.updateById(retryTask);
            }
            throw ex instanceof BusinessException ? (BusinessException) ex : new BusinessException("retry failed: " + ex.getMessage(), ex);
        }
    }

    private ApiExchangeLog createInitialLog(String traceId, String interfaceCode, Map<String, Object> sourceRequest) {
        ApiExchangeLog log = new ApiExchangeLog();
        log.traceId = traceId;
        log.interfaceCode = interfaceCode;
        log.sourceRequestRaw = toJson(sourceRequest);
        log.status = StatusConstants.RECEIVED;
        log.createTime = LocalDateTime.now();
        log.updateTime = LocalDateTime.now();
        logService.save(log);
        return log;
    }

    private ApiInterface findEnabledInterface(String interfaceCode) {
        ApiInterface apiInterface = interfaceService.getOne(new QueryWrapper<ApiInterface>().eq("interface_code", interfaceCode), false);
        if (apiInterface == null) {
            throw new BusinessException("interface not found: " + interfaceCode);
        }
        if (!StatusConstants.ENABLED.equalsIgnoreCase(apiInterface.status)) {
            throw new BusinessException("interface is disabled: " + interfaceCode);
        }
        return apiInterface;
    }

    private SysSystem findEnabledSystem(String systemCode) {
        SysSystem system = systemService.getOne(new QueryWrapper<SysSystem>().eq("system_code", systemCode), false);
        if (system == null) {
            throw new BusinessException("target system not found: " + systemCode);
        }
        if (!StatusConstants.ENABLED.equalsIgnoreCase(system.status)) {
            throw new BusinessException("target system is disabled: " + systemCode);
        }
        return system;
    }

    private String buildTargetUrl(String baseUrl, String targetUrl) {
        if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")) {
            return targetUrl;
        }
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new BusinessException("target system base_url is required when target_url is relative");
        }
        return baseUrl.replaceAll("/+$", "") + "/" + targetUrl.replaceAll("^/+", "");
    }

    private void updateLog(ApiExchangeLog log, String status, Map<String, Object> targetRequest, String targetResponseRaw,
                           Map<String, Object> sourceResponse, String error, long start) {
        if (targetRequest != null) {
            log.targetRequestData = toJson(targetRequest);
        }
        if (targetResponseRaw != null) {
            log.targetResponseRaw = targetResponseRaw;
        }
        if (sourceResponse != null) {
            log.sourceResponseData = toJson(sourceResponse);
        }
        log.status = status;
        log.errorMsg = error;
        log.executeTime = System.currentTimeMillis() - start;
        log.updateTime = LocalDateTime.now();
        logService.updateById(log);
    }

    private void upsertRetryTask(String traceId, String interfaceCode, String errorMessage) {
        ApiRetryTask retryTask = retryTaskService.getOne(new QueryWrapper<ApiRetryTask>().eq("trace_id", traceId), false);
        if (retryTask == null) {
            retryTask = new ApiRetryTask();
            retryTask.traceId = traceId;
            retryTask.interfaceCode = interfaceCode;
            retryTask.retryCount = 0;
            retryTask.maxRetryCount = 3;
            retryTask.status = StatusConstants.RETRY_PENDING;
            retryTask.createTime = LocalDateTime.now();
        }
        retryTask.lastErrorMsg = errorMessage;
        retryTask.updateTime = LocalDateTime.now();
        retryTaskService.saveOrUpdate(retryTask);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return String.valueOf(value);
        }
    }
}
