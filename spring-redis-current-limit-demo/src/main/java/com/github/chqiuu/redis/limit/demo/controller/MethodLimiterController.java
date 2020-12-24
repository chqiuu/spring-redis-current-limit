package com.github.chqiuu.redis.limit.demo.controller;

import com.github.chqiuu.redis.limit.Constant;
import com.github.chqiuu.redis.limit.annotation.CurrentLimit;
import com.github.chqiuu.redis.limit.enums.LimitTypeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Method
 *
 * @author chqiu
 */
@RestController
@RequestMapping("/method")
public class MethodLimiterController {
    private static final AtomicInteger ATOMIC_INTEGER_LOCAL = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_IP = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_USER = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_SESSION = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_CUSTOM = new AtomicInteger();

    @CurrentLimit(interval = 5, limit = 3, message = "此方法对所有来源的请求都限流，5秒只能访问3次，您已经超过3次访问，请稍后再试")
    @GetMapping("/local")
    public String local() {
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_LOCAL.incrementAndGet());
    }

    @CurrentLimit(interval = 5, limit = 3
            , message = "此方法访问根据IP限流，5秒只能访问3次，您已经超过3次访问，请稍后再试")
    @GetMapping("/ip")
    public String ip() {
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_IP.incrementAndGet());
    }

    @CurrentLimit(interval = 5, limit = 3
            , message = "此方法访问根据用户限流，5秒只能访问3次，您已经超过3次访问，请稍后再试")
    @GetMapping("/user")
    public String user() {
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_USER.incrementAndGet());
    }

    @CurrentLimit(interval = 5, limit = 3
            , message = "此方法访问根据Session限流，5秒只能访问3次，您已经超过3次访问，请稍后再试")
    @GetMapping("/session")
    public String session() {
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_SESSION.incrementAndGet());
    }

    @CurrentLimit(interval = 5, limit = 3, limitType = LimitTypeEnum.CUSTOM
            , message = "此方法为自定义限流，5秒只能访问3次，您已经超过3次访问，请稍后再试")
    @GetMapping("/custom")
    public String testLimiter2(HttpServletRequest request) {
        //根据一系列操作查出来了用户id
        //限流时在httpServletRequest中根据Const.CUSTOM的值进行限流
        request.setAttribute(Constant.CUSTOM, "user_id");
        return String.format("第%s次访问，成功获得数据！", ATOMIC_INTEGER_CUSTOM.incrementAndGet());
    }
}
