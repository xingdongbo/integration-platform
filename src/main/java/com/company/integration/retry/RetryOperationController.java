package com.company.integration.retry;

import com.company.integration.common.ApiResponse;
import com.company.integration.exchange.ExchangeResult;
import com.company.integration.exchange.ExchangeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retry-operations")
public class RetryOperationController {
    private final ExchangeService exchangeService;

    public RetryOperationController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/{traceId}/retry")
    public ApiResponse<ExchangeResult> retry(@PathVariable String traceId) {
        return ApiResponse.success(exchangeService.retry(traceId));
    }
}
