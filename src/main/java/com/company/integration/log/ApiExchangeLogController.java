package com.company.integration.log;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exchange-logs")
public class ApiExchangeLogController extends BaseController<ApiExchangeLog> {
    private final ApiExchangeLogService service;

    public ApiExchangeLogController(ApiExchangeLogService service) {
        super(service);
        this.service = service;
    }
}
