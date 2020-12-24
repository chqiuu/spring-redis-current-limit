package com.github.chqiuu.redis.limit.demo.controller;

import com.github.chqiuu.redis.limit.Constant;
import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chqiu
 */
@CurrentLimit(interval = 20, limit = 6, message = "class，您的手速太快了，请稍后再试")
@RestController
public class CompositeLimiterController {
    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_2 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_3 = new AtomicInteger();

    @CurrentLimit(interval = 20, limit = 5, message = "ALL，您的手速太快了，请稍后再试")
    @GetMapping("/limitTest1")
    public int testLimiter1() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }

    @CurrentLimit(interval = 20, limit = 3, limitType = LimitTypeEnum.CUSTOM)
    @GetMapping("/limitTest2")
    public int testLimiter2(HttpServletRequest request) {
        //根据一系列操作查出来了用户id
        //限流时在httpServletRequest中根据Const.CUSTOM的值进行限流
        request.setAttribute(Constant.CUSTOM, "用户id");
        return ATOMIC_INTEGER_2.incrementAndGet();
    }

    @CurrentLimit(interval = 20, limit = 5, limitType = LimitTypeEnum.IP, message = "IP，您的手速太快了，请稍后再试")
    @GetMapping("/limitTest3")
    public int testLimiter3() {
        return ATOMIC_INTEGER_3.incrementAndGet();
    }
}
