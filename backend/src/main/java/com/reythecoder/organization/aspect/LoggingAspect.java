package com.reythecoder.organization.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 日志切面 - 记录 Controller 和 Service 公共方法的执行日志
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * 定义切点：匹配所有 Controller 和 Service 的公共方法
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Service *)")
    public void controllerAndServicePointcut() {
    }

    /**
     * 定义切点：匹配所有公共方法
     */
    @Pointcut("execution(public * *(..))")
    public void publicMethodPointcut() {
    }

    /**
     * 环绕通知：记录方法执行日志
     *
     * @param joinPoint 连接点
     * @return 方法返回值
     * @throws Throwable 方法执行异常
     */
    @Around("controllerAndServicePointcut() && publicMethodPointcut()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String methodSignature = className + "." + methodName;

        // 记录方法开始执行
        logger.info("开始执行方法：{}", methodSignature);

        long startTime = System.currentTimeMillis();
        Object result;

        try {
            // 执行目标方法
            result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录方法执行成功
            logger.info("方法执行成功：{} | 耗时：{}ms", methodSignature, duration);

            return result;
        } catch (Throwable e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录方法执行异常
            logger.error("方法执行异常：{} | 耗时：{}ms | 异常：{}", methodSignature, duration, e.getMessage(), e);

            throw e;
        }
    }
}
