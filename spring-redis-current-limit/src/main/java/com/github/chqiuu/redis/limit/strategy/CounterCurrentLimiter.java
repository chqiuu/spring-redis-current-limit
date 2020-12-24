package com.github.chqiuu.redis.limit.strategy;

import cn.hutool.core.util.StrUtil;
import com.github.chqiuu.redis.limit.Constant;
import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
import com.github.chqiuu.redis.limit.enums.TypeLimitModelEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 计数器算法限流实现
 *
 * @author chqiu
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = Constant.PREFIX, name = "algorithm", havingValue = "counter")
public class CounterCurrentLimiter extends BaseCurrentLimiter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final DefaultRedisScript<Long> counterRedisScript;

    private boolean check(String key, long limit, long interval) {
        List<String> keyList = new ArrayList<>();
        keyList.add(key);
        keyList.add(String.valueOf(limit));
        keyList.add(String.valueOf(interval));
        Long executeTimes = redisTemplate.execute(counterRedisScript, keyList, keyList);
        if (executeTimes != null) {
            if (executeTimes <= 0) {
                // log.error("【{}】在单位时间 {} 秒内已达到访问上限，当前接口上限 {}", key, interval, limit);
                return false;
            } else {
                //  log.info("【{}】在单位时间 {} 秒内可访问 {} 次", key, interval, executeTimes - 1);
                return true;
            }
        } else {
            log.error("限流服务器错误：【{}】在单位时间 {} 秒内已达到访问上限，当前接口上限 {}", key, interval, limit);
            return false;
        }
    }

    @Override
    public boolean check(JoinPoint joinPoint, boolean isMethod, CurrentLimit currentLimit) {
        String key = currentLimit.key();
        if (StrUtil.isEmpty(key)) {
            key = getCurrentKey(joinPoint, isMethod, currentLimit.limitType(), currentLimit.typeLimitModel());
        }
        return check(key, currentLimit.limit(), currentLimit.interval());
    }
}
