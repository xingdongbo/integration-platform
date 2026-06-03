package com.company.integration.retry;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retry-tasks")
public class ApiRetryTaskController extends BaseController<ApiRetryTask> {
    private final ApiRetryTaskService service;

    public ApiRetryTaskController(ApiRetryTaskService service) {
        super(service);
        this.service = service;
    }
}
