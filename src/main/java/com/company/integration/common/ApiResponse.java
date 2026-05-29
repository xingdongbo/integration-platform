package com.company.integration.common;

public class ApiResponse<T> {
    public int code;
    public String message;
    public T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
