package com.company.integration.exchange;

import java.util.Map;

public class ExchangeResult {
    public String traceId;
    public Map<String, Object> targetRequest;
    public String targetResponseRaw;
    public Map<String, Object> sourceResponse;

    public ExchangeResult() {
    }

    public ExchangeResult(String traceId, Map<String, Object> targetRequest, String targetResponseRaw, Map<String, Object> sourceResponse) {
        this.traceId = traceId;
        this.targetRequest = targetRequest;
        this.targetResponseRaw = targetResponseRaw;
        this.sourceResponse = sourceResponse;
    }
}
