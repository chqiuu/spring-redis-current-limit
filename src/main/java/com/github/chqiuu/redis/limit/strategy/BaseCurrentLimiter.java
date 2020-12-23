package com.github.chqiuu.redis.limit.strategy;

import cn.hutool.extra.servlet.ServletUtil;
import com.github.chqiuu.redis.limit.Constant;
import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
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
     * @param joinPoint 切点
     * @param isMethod  切点位置是否为方法，反之为对象
     * @param key       限流条件唯一标识
     * @param limitType 限流方式
     * @param limit     允许访问多少次
     * @param interval  间隔时间
     * @param step      递减步长
     * @return 是否运行访问
     */
    public abstract boolean check(JoinPoint joinPoint, boolean isMethod, String key, LimitTypeEnum limitType, long limit, long interval, long step);

    /**
     * 获取唯一标识此次请求的key
     *
     * @param joinPoint 切点
     * @param limitType 限流类型枚举
     * @param isMethod  是否为方法
     * @return key
     */
    String getCurrentKey(JoinPoint joinPoint, LimitTypeEnum limitType, boolean isMethod) {
        StringBuilder key = new StringBuilder(Constant.HASH_TAG);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        switch (limitType) {
            case SESSION_GLOBAL:
                // 以SessionId作为key
                if (request.getSession() != null) {
                    key.append(request.getSession().getId());
                } else {
                    throw new CurrentLimitException("获取不到用户Session，request.getSession().getId()为空");
                }
                break;
            case SESSION:
                // 以SessionId加方法（或类）作为key
                if (request.getSession() != null) {
                    key.append(request.getSession().getId());
                    if (isMethod) {
                        key.append(getMethodName((MethodSignature) joinPoint.getSignature()));
                    }
                } else {
                    throw new CurrentLimitException("获取不到用户Session，request.getSession().getId()为空");
                }
                break;
            case USER_GLOBAL:
                // 以用户信息作为key
                if (request.getUserPrincipal() != null) {
                    key.append(request.getUserPrincipal().getName());
                } else {
                    throw new CurrentLimitException("用户未登录，request.getUserPrincipal().getName()为空");
                }
                break;
            case USER:
                // 以用户信息加方法（或类）作为key
                if (request.getUserPrincipal() != null) {
                    key.append(request.getUserPrincipal().getName());
                    if (isMethod) {
                        key.append(getMethodName((MethodSignature) joinPoint.getSignature()));
                    }
                    key.append(joinPoint.getTarget().getClass());
                } else {
                    throw new CurrentLimitException("用户未登录，request.getUserPrincipal().getName()为空");
                }
                break;
            case IP_GLOBAL:
                // 以IP地址作为key
                key.append(ServletUtil.getClientIP(request, ""));
                break;
            case IP:
                // 以IP地址加方法（或类）作为key
                key.append(ServletUtil.getClientIP(request, ""));
                if (isMethod) {
                    key.append(getMethodName((MethodSignature) joinPoint.getSignature()));
                }
                key.append(joinPoint.getTarget().getClass());
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
                // 以方法名加参数列表作为唯一标识方法的key
                if (isMethod) {
                    key.append(getMethodName((MethodSignature) joinPoint.getSignature()));
                }
                key.append(joinPoint.getTarget().getClass());
                break;
        }
        return key.toString();
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
