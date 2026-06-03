package com.company.integration.mapping;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ResponseMappingEngine {
    private final MappingEngine mappingEngine;

    public ResponseMappingEngine(MappingEngine mappingEngine) {
        this.mappingEngine = mappingEngine;
    }

    public Map<String, Object> map(Map<String, Object> targetResponse, List<ApiMappingResponse> rules) {
        return rules == null || rules.isEmpty() ? targetResponse : mappingEngine.mapResponse(targetResponse, rules);
    }
}
