package com.company.integration.header;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/headers")
public class ApiHeaderController extends BaseController<ApiHeader> {
    private final ApiHeaderService service;

    public ApiHeaderController(ApiHeaderService service) {
        super(service);
        this.service = service;
    }

    @GetMapping("/by-interface/{interfaceCode}")
    public ApiResponse<java.util.List<ApiHeader>> listByInterfaceCode(@PathVariable String interfaceCode) {
        return ApiResponse.success(service.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ApiHeader>()
                .eq("interface_code", interfaceCode)));
    }
}
