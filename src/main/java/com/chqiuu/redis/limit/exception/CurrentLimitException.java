package com.chqiuu.redis.limit.exception;

/**
 * 限流异常信息类
 *
 * @author chqiu
 */
public class CurrentLimitException extends RuntimeException {
    private String message;

    public CurrentLimitException() {
        super();
    }

    public CurrentLimitException(String message) {
        super(message);
    }
}
