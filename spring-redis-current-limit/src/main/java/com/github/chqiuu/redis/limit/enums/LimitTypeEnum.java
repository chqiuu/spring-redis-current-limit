package com.github.chqiuu.redis.limit.enums;

/**
 * 限流类型
 *
 * @author chqiu
 */
public enum LimitTypeEnum {
    /**
     * 所有来源的请求统一限流。此方法（或类）单位时间内只允许访问n次
     */
    LOCAL,
    /**
     * 根据IP限流。此方法（或类）单位时间内只允许此IP访问n次
     */
    IP,
    /**
     * 根据用户限流。此方法（或类）单位时间内只允许此用户访问n次
     */
    USER,
    /**
     * 根据Session限流。此方法（或类）单位时间内只允许此 SessionId 访问n次
     */
    SESSION,
    /**
     * 自定义限流方法，详细使用请参考使用文档
     */
    CUSTOM
}
