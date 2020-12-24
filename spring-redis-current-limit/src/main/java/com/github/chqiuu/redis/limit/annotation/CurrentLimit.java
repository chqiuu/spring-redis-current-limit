package com.github.chqiuu.redis.limit.annotation;


import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
import com.github.chqiuu.redis.limit.enums.TypeLimitModelEnum;

import java.lang.annotation.*;

/**
 * 限流注解
 *
 * @author chqiu
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CurrentLimit {
    /**
     * 设置限流条件唯一标识，默认为系统自动生成。建议不配置此项，若想要配置请保证此项的唯一性
     *
     * @return 限流条件唯一标识
     */
    String key() default "";

    /**
     * 设置提示消息
     *
     * @return 被限流时的提示消息
     */
    String message() default "您的手速太快了，请稍后再试";

    /**
     * 设置给定的时间范围 单位(秒)
     *
     * @return 给定的时间范围 单位(秒)。默认值60
     */
    long interval() default 60;

    /**
     * 设置单位时间内可访问次数（限流次数）
     * 当为令牌桶算法时，为向令牌桶中添加数据的时间间隔, 以秒为单位。默认值10秒
     *
     * @return 单位时间内可访问次数（限流次数）。默认值10
     */
    long limit() default 10;

    /**
     * 设置每次为令牌桶中添加的令牌数量
     *
     * @return 每次为令牌桶中添加的令牌数量。默认值5个
     */
    long step() default 5;

    /**
     * 设置限流类型。默认值：LOCAL
     */
    LimitTypeEnum limitType() default LimitTypeEnum.LOCAL;

    /**
     * 设置在类上设置注解限流模式，当为ElementType.TYPE时有效
     *
     * @return 在类上设置注解限流模式
     */
    TypeLimitModelEnum typeLimitModel() default TypeLimitModelEnum.EACH;
}
