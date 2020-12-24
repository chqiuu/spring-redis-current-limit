package com.github.chqiuu.redis.limit;

/**
 * 全局静态常量类
 * @author chqiu
 */
public class Constant {
    /**
     * 配置current-limit的前缀
     */
    public static final String PREFIX = "current-limit";
    /**
     * 集群模式指定slot的hash tag
     */
    public static final String HASH_TAG = "{current-limit}";
    /**
     * 自定义拦截方式时的key
     */
    public static final String CUSTOM = "current-limit-custom";
}
