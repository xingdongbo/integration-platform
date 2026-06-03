package com.company.integration.mapping;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RequestMappingEngine {
    private final MappingEngine mappingEngine;

    public RequestMappingEngine(MappingEngine mappingEngine) {
        this.mappingEngine = mappingEngine;
    }

    public Map<String, Object> map(Map<String, Object> sourceRequest, List<ApiMappingRequest> rules) {
        return rules == null || rules.isEmpty() ? sourceRequest : mappingEngine.mapRequest(sourceRequest, rules);
    }
}
