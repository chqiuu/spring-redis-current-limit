package com.chqiuu.redis.limit.aspect;

import com.chqiuu.redis.limit.annotation.CurrentLimit;
import com.chqiuu.redis.limit.exception.CurrentLimitException;
import com.chqiuu.redis.limit.strategy.BaseCurrentLimiter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 限流切面实现（对象）
 * 对象切面优先于方法切面
 *
 * @author chqiu
 */
@Order(1)
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ObjectCurrentLimitAspect {

    private final BaseCurrentLimiter currentLimiter;

    /**
     * 将类标记为切入点
     *
     * @param currentLimit 注解
     */
    @Pointcut("@within(currentLimit)")
    public void withinPointcut(CurrentLimit currentLimit) {
    }

    /**
     * 将类标记为在切入点覆盖的方法之前执行的通知
     *
     * @param joinPoint    切点
     * @param currentLimit 注解
     */
    @Before(value = "withinPointcut(currentLimit)", argNames = "joinPoint,currentLimit")
    public void doWithinBefore(JoinPoint joinPoint, CurrentLimit currentLimit) {
        boolean isAllowAccess = currentLimiter.check(joinPoint, true, currentLimit.key(), currentLimit.limitType(), currentLimit.limit(), currentLimit.interval(), currentLimit.step());
        if (!isAllowAccess) {
            throw new CurrentLimitException(currentLimit.message());
        }
    }
}
