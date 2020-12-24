package com.github.chqiuu.redis.limit.demo;

import cn.hutool.core.util.StrUtil;
import com.github.chqiuu.redis.limit.annotation.EnableCurrentLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author chqiu
 */
@Slf4j
@EnableCurrentLimit
@SpringBootApplication
public class SpringRedisCurrentLimitDemoApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(SpringRedisCurrentLimitDemoApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = "80".equals(env.getProperty("server.port")) ? "" : (":" + env.getProperty("server.port"));
        String path = StrUtil.isBlank(env.getProperty("server.servlet.context-path")) ? "" : env.getProperty("server.servlet.context-path");
        String applicationName = env.getProperty("spring.application.name");
        log.info("\n----------------------------------------------------------\n\t" +
                applicationName + "，访问地址:\n\t" +
                "Local: \t\thttp://localhost" + port + path + "\n\t" +
                "External: \thttp://" + ip + port + path + "\n\t" +
                "----------------------------------------------------------");
    }
}
