package com.chqiuu.redis.limit.annotation;

import com.chqiuu.redis.limit.config.EnableCurrentLimitConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启限流功能的注解
 *
 * @author chqiu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableCurrentLimitConfiguration.class)
@Documented
@Inherited
public @interface EnableCurrentLimit {
}
