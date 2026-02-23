package com.reythecoder.organization.dto.response;

public record ApiResult<T>(
        int status,
        String message,
        T data) {
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    public static <T> ApiResult<T> error(int status, String message) {
        return new ApiResult<>(status, message, null);
    }

    public static <T> ApiResult<T> error(int status, String message, T data) {
        return new ApiResult<>(status, message, data);
    }
}
