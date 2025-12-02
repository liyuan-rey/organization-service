package com.reythecoder.organization.exception;

import com.reythecoder.organization.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义API异常
    @ExceptionHandler(ApiException.class)
    public ApiResponse<Object> handleApiException(ApiException e) {
        logger.error("API异常: {}", e.getMessage(), e);
        return ApiResponse.error(e.getStatus(), e.getMessage());
    }

    // 处理方法参数验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证异常: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    // 处理约束违反异常
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse<Object> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("约束违反异常: {}", e.getMessage(), e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("约束违反");
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    // 处理方法参数类型不匹配异常
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("参数类型不匹配异常: {}", e.getMessage(), e);
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s",
                e.getName(), e.getRequiredType().getSimpleName());
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    // 处理HTTP消息不可读异常
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("HTTP消息不可读异常: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "请求体格式错误");
    }

    // 处理所有其他未捕获的异常
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误");
    }
}
