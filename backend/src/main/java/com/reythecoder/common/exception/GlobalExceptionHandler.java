package com.reythecoder.common.exception;

import com.reythecoder.common.dto.ApiResult;
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

    @ExceptionHandler(ApiException.class)
    public ApiResult<Object> handleApiException(ApiException e) {
        logger.error("API异常: {}", e.getMessage(), e);
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证异常: {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResult<Object> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("约束违反异常: {}", e.getMessage(), e);
        String errorMessage = e.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("约束违反");
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("参数类型不匹配异常: {}", e.getMessage(), e);
        var expectedType = e.getRequiredType();
        String typeName = expectedType != null ? expectedType.getSimpleName() : "未知";
        String errorMessage = String.format("参数 '%s' 类型不匹配，期望类型: %s", e.getName(), typeName);
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("HTTP消息不可读异常: {}", e.getMessage(), e);
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Object> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ApiResult.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统内部错误");
    }
}