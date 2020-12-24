package com.github.chqiuu.redis.limit.strategy;

import cn.hutool.extra.servlet.ServletUtil;
import com.github.chqiuu.redis.limit.Constant;
import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
import com.github.chqiuu.redis.limit.enums.TypeLimitModelEnum;
import com.github.chqiuu.redis.limit.exception.CurrentLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流器算法策略接口
 *
 * @author chqiu
 */
public abstract class BaseCurrentLimiter {

    /**
     * 限流器算法 检查是否运行访问
     *
     * @param joinPoint    切点
     * @param isMethod     切点位置是否为方法，反之为对象
     * @param currentLimit 注解
     * @return 是否运行访问
     */
    public abstract boolean check(JoinPoint joinPoint, boolean isMethod, CurrentLimit currentLimit);

    /**
     * 获取唯一标识此次请求的key
     *
     * @param joinPoint          切点
     * @param isMethod
     * @param limitType          限流类型枚举
     * @param typeLimitModelEnum 是否为方法
     * @return key
     */
    String getCurrentKey(JoinPoint joinPoint, boolean isMethod, LimitTypeEnum limitType, TypeLimitModelEnum typeLimitModelEnum) {
        StringBuilder key = new StringBuilder(Constant.HASH_TAG).append(getBaseKey(isMethod, typeLimitModelEnum, joinPoint));
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        switch (limitType) {
            case SESSION:
                // 以SessionId加方法（或类）作为key
                if (request.getSession() != null) {
                    key.append(request.getSession().getId());
                } else {
                    throw new CurrentLimitException("获取不到用户Session，request.getSession().getId()为空");
                }
                break;
            case USER:
                // 以用户信息加方法（或类）作为key
                if (request.getUserPrincipal() != null) {
                    key.append(request.getUserPrincipal().getName());
                } else {
                    throw new CurrentLimitException("用户未登录，request.getUserPrincipal().getName()为空");
                }
                break;
            case IP:
                // 以IP地址加方法（或类）作为key
                key.append(ServletUtil.getClientIP(request, ""));
                break;
            case CUSTOM:
                // 以自定义内容作为key
                if (request.getAttribute(Constant.CUSTOM) != null) {
                    key.append(request.getAttribute(Constant.CUSTOM).toString());
                } else {
                    throw new CurrentLimitException("没有找到自定义KEY，请检查request.getAttribute('current-limit-custom')是否设置");
                }
                break;
            default:
                break;
        }
        return key.toString();
    }

    private String getBaseKey(boolean isMethod, TypeLimitModelEnum typeLimitModelEnum, JoinPoint joinPoint) {
        StringBuilder key = new StringBuilder();
        if (isMethod) {
            key.append(getMethodName((MethodSignature) joinPoint.getSignature())).append(joinPoint.getTarget().getClass());
        } else {
            switch (typeLimitModelEnum) {
                case EACH:
                    // 以方法名加参数列表作为唯一标识方法的key
                    key.append(getMethodName((MethodSignature) joinPoint.getSignature())).append(joinPoint.getTarget().getClass());
                    break;
                case TOTAL:
                    key.append(joinPoint.getTarget().getClass());
                    break;
                default:
                    // 对项目所有接口限流
                    key.append(typeLimitModelEnum.name());
                    break;
            }
        }
        return String.valueOf(key);
    }

    /**
     * 获取方法名及方法参数
     *
     * @param signature MethodSignature
     * @return 方法名及方法参数
     */
    private String getMethodName(MethodSignature signature) {
        StringBuilder key = new StringBuilder();
        key.append(signature.getMethod().getName());
        Class<?>[] parameterTypes = signature.getParameterTypes();
        for (Class<?> clazz : parameterTypes) {
            key.append(clazz.getName());
        }
        return key.toString();
    }
}
