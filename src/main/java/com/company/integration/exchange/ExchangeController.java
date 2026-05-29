package com.company.integration.exchange;

import com.company.integration.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @PostMapping("/{interfaceCode}")
    public ApiResponse<Map<String, Object>> exchange(@PathVariable String interfaceCode,
                                                     @RequestBody Map<String, Object> sourceRequest) {
        ExchangeResult result = exchangeService.exchange(interfaceCode, sourceRequest);
        return new ApiResponse<>(0, result.traceId, result.sourceResponse);
    }

    @PostMapping("/{interfaceCode}/test")
    public ApiResponse<ExchangeResult> test(@PathVariable String interfaceCode,
                                            @RequestBody Map<String, Object> sourceRequest) {
        return ApiResponse.success(exchangeService.exchange(interfaceCode, sourceRequest));
    }
}
