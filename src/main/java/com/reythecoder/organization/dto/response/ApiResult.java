package com.reythecoder.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .status(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(int status, String message) {
        return ApiResult.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ApiResult<T> error(int status, String message, T data) {
        return ApiResult.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}
