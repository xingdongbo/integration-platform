package com.company.integration.mapping;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/request-mappings")
public class ApiMappingRequestController extends BaseController<ApiMappingRequest> {
    private final ApiMappingRequestService service;

    public ApiMappingRequestController(ApiMappingRequestService service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/by-interface/{interfaceCode}")
    public ApiResponse<java.util.List<ApiMappingRequest>> listByInterfaceCode(@PathVariable String interfaceCode) {
        return ApiResponse.success(service.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ApiMappingRequest>()
                .eq("interface_code", interfaceCode)
                .orderByAsc("sort")));
    }
}
