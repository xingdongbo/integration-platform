package com.company.integration.api;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interfaces")
public class ApiInterfaceController extends BaseController<ApiInterface> {
    private final ApiInterfaceService service;

    public ApiInterfaceController(ApiInterfaceService service) {
        super(service);
        this.service = service;
    }
}
