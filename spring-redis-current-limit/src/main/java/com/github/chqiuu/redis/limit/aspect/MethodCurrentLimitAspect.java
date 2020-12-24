package com.github.chqiuu.redis.limit.aspect;

import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.exception.CurrentLimitException;
import com.github.chqiuu.redis.limit.strategy.BaseCurrentLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 限流切面实现（面向方法）
 *
 * @author chqiu
 */
@Order(2)
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class MethodCurrentLimitAspect {

    private final BaseCurrentLimiter currentLimiter;

    /**
     * 将方法标记为切入点
     *
     * @param currentLimit 注解
     */
    @Pointcut("@annotation(currentLimit)")
    public void annotationPointcut(CurrentLimit currentLimit) {
    }

    /**
     * 将方法标记为在切入点覆盖的方法之前执行的通知
     *
     * @param joinPoint    切点
     * @param currentLimit 注解
     */
    @Before(value = "annotationPointcut(currentLimit)", argNames = "joinPoint,currentLimit")
    public void doAnnotationBefore(JoinPoint joinPoint, CurrentLimit currentLimit) {
        boolean isAllowAccess = currentLimiter.check(joinPoint, true, currentLimit);
        if (!isAllowAccess) {
            throw new CurrentLimitException(currentLimit.message());
        }
    }
}
