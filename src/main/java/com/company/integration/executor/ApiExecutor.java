package com.company.integration.executor;

import com.company.integration.api.ApiInterface;
import com.company.integration.header.ApiHeader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class ApiExecutor {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final ObjectMapper objectMapper;

    public ApiExecutor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public TargetApiResult execute(ApiInterface apiInterface, String url, List<ApiHeader> headers, Map<String, Object> payload) throws IOException {
        int timeout = apiInterface.timeout == null ? 5000 : apiInterface.timeout;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofMillis(timeout))
                .readTimeout(Duration.ofMillis(timeout))
                .writeTimeout(Duration.ofMillis(timeout))
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();

        Request.Builder builder = new Request.Builder().url(buildUrl(apiInterface.method, url, payload));
        for (ApiHeader header : headers) {
            builder.header(header.headerName, header.headerValue);
        }
        builder.header("Content-Type", apiInterface.contentType == null ? "application/json" : apiInterface.contentType);

        String method = apiInterface.method == null ? "POST" : apiInterface.method.toUpperCase();
        if ("GET".equals(method)) {
            builder.get();
        } else {
            builder.method(method, RequestBody.create(objectMapper.writeValueAsString(payload), JSON));
        }

        try (Response response = client.newCall(builder.build()).execute()) {
            String body = response.body() == null ? "" : response.body().string();
            return new TargetApiResult(response.code(), response.isSuccessful(), body, parseBody(body));
        }
    }

    private String buildUrl(String method, String url, Map<String, Object> payload) {
        if (!"GET".equalsIgnoreCase(method)) {
            return url;
        }
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : payload.entrySet()) {
            if (entry.getValue() != null) {
                builder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return builder.build().toString();
    }

    private Map<String, Object> parseBody(String body) throws IOException {
        if (body == null || body.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(body, new TypeReference<>() {});
        } catch (Exception ex) {
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("raw", body);
            return wrapper;
        }
    }
}
