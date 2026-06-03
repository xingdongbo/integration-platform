package com.company.integration.mapping;

import com.company.integration.common.BusinessException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MappingEngine {
    public Map<String, Object> mapRequest(Map<String, Object> source, List<ApiMappingRequest> rules) {
        Map<String, Object> target = new LinkedHashMap<>();
        for (ApiMappingRequest rule : rules) {
            applyRule(source, target, rule.sourceField, rule.targetField, rule.mappingType, rule.defaultValue, rule.expression);
        }
        return target;
    }

    public Map<String, Object> mapResponse(Map<String, Object> source, List<ApiMappingResponse> rules) {
        Map<String, Object> target = new LinkedHashMap<>();
        for (ApiMappingResponse rule : rules) {
            applyRule(source, target, rule.sourceField, rule.targetField, rule.mappingType, rule.defaultValue, rule.expression);
        }
        return target;
    }

    private void applyRule(Map<String, Object> source, Map<String, Object> target, String sourceField,
                           String targetField, String mappingType, String defaultValue, String expression) {
        if (targetField == null || targetField.isBlank()) {
            throw new BusinessException("mapping target_field is required");
        }
        Object value = switch (mappingType == null ? "" : mappingType.toUpperCase()) {
            case "FIELD" -> readPath(source, sourceField);
            case "CONST" -> defaultValue;
            case "DEFAULT" -> {
                Object sourceValue = readPath(source, sourceField);
                yield isEmpty(sourceValue) ? defaultValue : sourceValue;
            }
            case "EXPRESSION" -> evaluateExpression(source, expression, sourceField, defaultValue);
            default -> throw new BusinessException("unsupported mapping_type: " + mappingType);
        };
        writePath(target, targetField, value);
    }

    private Object evaluateExpression(Map<String, Object> source, String expression, String sourceField, String defaultValue) {
        Object value = readPath(source, sourceField);
        if (expression == null || expression.isBlank()) {
            return isEmpty(value) ? defaultValue : value;
        }
        String trimmed = expression.trim();
        if (trimmed.startsWith("concat(")) {
            String inner = trimmed.substring("concat(".length(), trimmed.length() - 1);
            StringBuilder result = new StringBuilder();
            for (String part : inner.split(",")) {
                String token = part.trim();
                if (token.startsWith("'") && token.endsWith("'")) {
                    result.append(token, 1, token.length() - 1);
                } else {
                    Object partValue = readPath(source, token);
                    if (partValue != null) {
                        result.append(partValue);
                    }
                }
            }
            return result.toString();
        }
        if (trimmed.startsWith("default(")) {
            return isEmpty(value) ? trimmed.substring("default(".length(), trimmed.length() - 1) : value;
        }
        throw new BusinessException("unsupported expression: " + expression);
    }

    @SuppressWarnings("unchecked")
    public Object readPath(Map<String, Object> source, String path) {
        if (source == null || path == null || path.isBlank()) {
            return null;
        }
        Object current = source;
        for (String part : path.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = ((Map<String, Object>) map).get(part);
        }
        return current;
    }

    @SuppressWarnings("unchecked")
    public void writePath(Map<String, Object> target, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = target;
        for (int i = 0; i < parts.length - 1; i++) {
            Object next = current.get(parts[i]);
            if (!(next instanceof Map<?, ?>)) {
                next = new LinkedHashMap<String, Object>();
                current.put(parts[i], next);
            }
            current = (Map<String, Object>) next;
        }
        current.put(parts[parts.length - 1], value);
    }

    private boolean isEmpty(Object value) {
        return value == null || (value instanceof String text && text.isBlank());
    }
}
