package com.github.chqiuu.redis.limit.demo.controller;

import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.enums.TypeLimitModelEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chqiu
 */
@CurrentLimit(interval = 5, limit = 3, typeLimitModel = TypeLimitModelEnum.TOTAL, message = "在类上做限流，此类中（方法不区分）每5秒只能访问3次，您已经超过3次访问，请稍后再试")
@RestController
@RequestMapping("/type/total")
public class TypeTotalCurrentLimitController {
    private static final AtomicInteger ATOMIC_INTEGER_HAVE_PARAM = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_NO_PARAM = new AtomicInteger();

    @GetMapping("/haveParam")
    public String haveParam(String param) {
        return String.format("第%s次访问【param:%s】，成功获得数据！", ATOMIC_INTEGER_HAVE_PARAM.incrementAndGet(), param);
    }

    @GetMapping("/noParam")
    public String noParam() {
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_NO_PARAM.incrementAndGet());
    }
}
