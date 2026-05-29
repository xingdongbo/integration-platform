package com.company.integration.mapping;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/response-mappings")
public class ApiMappingResponseController extends BaseController<ApiMappingResponse> {
    private final ApiMappingResponseService service;

    public ApiMappingResponseController(ApiMappingResponseService service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/by-interface/{interfaceCode}")
    public ApiResponse<java.util.List<ApiMappingResponse>> listByInterfaceCode(@PathVariable String interfaceCode) {
        return ApiResponse.success(service.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ApiMappingResponse>()
                .eq("interface_code", interfaceCode)
                .orderByAsc("sort")));
    }
}
