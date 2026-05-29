package com.company.integration.executor;

import java.util.Map;

public class TargetApiResult {
    public final int statusCode;
    public final boolean successful;
    public final String rawBody;
    public final Map<String, Object> body;

    public TargetApiResult(int statusCode, boolean successful, String rawBody, Map<String, Object> body) {
        this.statusCode = statusCode;
        this.successful = successful;
        this.rawBody = rawBody;
        this.body = body;
    }
}
