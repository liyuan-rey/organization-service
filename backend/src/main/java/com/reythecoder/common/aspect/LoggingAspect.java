package com.reythecoder.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public void controllerAndServicePointcut() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethodPointcut() {
    }

    @Around("controllerAndServicePointcut() && publicMethodPointcut()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String methodSignature = className + "." + methodName;

        logger.info("开始执行方法：{}", methodSignature);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.info("方法执行成功：{} | 耗时：{}ms", methodSignature, duration);

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            logger.error("方法执行异常：{} | 耗时：{}ms | 异常：{}", methodSignature, duration, e.getMessage(), e);

            throw e;
        }
    }
}