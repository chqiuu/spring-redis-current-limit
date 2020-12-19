# spring-redis-current-limit
***

## 项目介绍
> 此项目为一个无侵入的应用级网关限流框架,如果您正在寻找一个网关限流的框架，使用spring-redis-current-limit是最明智的选择

### 为什么选择spring-redis-current-limit
1. 无需任何复杂配置文件，一个注解玩转spring-redis-current-limit<br>
2.  细粒度控制，您可以控制同一个类中的A方法每分钟限流100而B方法每分钟限流200<br>
3.  高灵活性，可根据自定义信息（如用户id、用户ip、用户权限等）进行限流、可灵活选择限流算法<br>
4.  高可用性，使用redis+lua脚本的原子性为分布式系统保驾护航<br>
5.  高可扩展性，可灵活添加限流算法<br>
## Quick Start
### 1.  引入spring-redis-current-limit
```xml
<dependency>
    <groupId>com.chqiuu</groupId>
    <artifactId>spring-redis-current-limit</artifactId>
    <version>1.0.0</version>
 </dependency>
```
### 2.  注册spring-redis-current-limit
>因为并不是所有的项目都会使用SpringBoot,所以在注册这一步我们分为两种情况
#### 1.SpringBoot或SpringCloud项目
您需要在启动类上增加一个注解
```java
@EnableCurrentLimit
@SpringBootApplication
public class StudyApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}
}
```
#### 2.Spring项目
您需要提供一个可以被Spring管理的配置类。比如说：
```java
@Import(EnableCurrentLimitConfiguration.class)
@Configuration
public class CurrentLimitConfig {
}
```
### 3. 配置您的redis连接
>您需要配置您的redis连接为spring-redis-current-limit，同2的情况我们把项目分为两种情况（注意下方的配置需要根据实际情况调整）
#### 1.SpringBoot或SpringCloud项目
```yaml
spring:
  redis:
    host: 
    port: 
    password:
    pool:
      max-active: 8
      max-wait: 1
      max-idle: 8
      min-idle: 0
    timeout: 2000
```
#### 2. Spring应用
您只需要注册一个RedisConnectionFactory子类的bean。比如说
```xml
<beans>
    <bean id="poolConfig" class="redis.clients.jedis.JedisPoolConfig" >
        <property name="maxIdle" value="${redis.maxIdle}" />
        <property name="maxWaitMillis" value="${redis.maxWait}" />
        <property name="testOnBorrow" value="${redis.testOnBorrow}" />
    </bean>
    <bean id="connectionFactory"  class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory" >
        <property name="poolConfig" ref="poolConfig" />
        <property name="port" value="${redis.port}" />
        <property name="hostName" value="${redis.host}" />
        <property name="timeout" value="${redis.timeout}" ></property>
        <property name="database" value="1"></property>
    </bean>
</beans>
```
### 4.  使用spring-redis-current-limit
>其实看到这一步的时候您已经可以使用spring-redis-current-limit来进行限流了哦。<br>

##### spring-redis-current-limit为您提供了一个注解@CurrentLimit来进行限流。您可以将它用在类上或方法上。

接下来介绍一下注解中的几个属性

```java
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
}
```
来几个使用的例子吧<br>
1. 限流一个Controller中的所有接口。（例如，需要每个方法每30秒只允许调用10次）<br>
```java
@CurrentLimit(interval = 30, limit = 10, message = "class，您的手速太快了，请稍后再试")
@RestController
@RequestMapping("/object")
public class ObjectCurrentLimitController {

    @PostMapping("/havaParam")
    public void havaParam(@RequestBody Map<String, String> map) {
        System.out.println("业务代码havaParam……");
    }

    @GetMapping("/noParam")
    public void noParam() {
        System.out.println("业务代码noParam……");
    }
}
```
2.  根据IP限流访问次数
```java
//每个IP每20秒可以访问5次
@CurrentLimit(interval = 20, limit = 5, limitType = LimitTypeEnum.IP, message = "IP，您的手速太快了，请稍后再试")
```

4.  限流某个方法的并发数
```java
@CurrentLimit(interval = 20, limit = 6, message = "class，您的手速太快了，请稍后再试")// 对当前这个类进行整体限流
@RestController
public class LimiterController {
    private static final AtomicInteger ATOMIC_INTEGER_1 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_2 = new AtomicInteger();
    private static final AtomicInteger ATOMIC_INTEGER_3 = new AtomicInteger();

    @CurrentLimit(interval = 20, limit = 5, message = "ALL，您的手速太快了，请稍后再试")// 对这个方法进行整体限流
    @GetMapping("/limitTest1")
    public int testLimiter1() {
        return ATOMIC_INTEGER_1.incrementAndGet();
    }

    @CurrentLimit(interval = 20, limit = 3, limitType = LimitTypeEnum.CUSTOM)// 对这个方法进行自定义限流
    @GetMapping("/limitTest2")
    public int testLimiter2(HttpServletRequest request) {
        //根据一系列操作查出来了用户id
        //限流时在httpServletRequest中根据Const.CUSTOM的值进行限流
        request.setAttribute(Constant.CUSTOM, "用户id");
        return ATOMIC_INTEGER_2.incrementAndGet();
    }

    @CurrentLimit(interval = 20, limit = 5, limitType = LimitTypeEnum.IP, message = "IP，您的手速太快了，请稍后再试") // 对这个方法进行IP限流
    @GetMapping("/limitTest3")
    public int testLimiter3() {
        return ATOMIC_INTEGER_3.incrementAndGet();
    }
}
```


## 更多信息

>相信看完了上方的Quick Start您已经迫不及待的想要将spring-redis-current-limit应用于生产了。我在这里为您提供了两种限流算法。您可以根据自己系统的需求选择自己需要的算法

#### 限流算法
> 如果您对限流算法不太了解的话可以先参考一下这篇文章[高并发系统的限流方案研究，其实限流实现也不复杂](https://blog.csdn.net/QIU176161650/article/details/111291392)

1.  令牌桶算法<br>
程序默认使用令牌桶算法进行限流，如果您要使用令牌桶算法的话无需要额外的配置。
2.  计数器算法<br>
    如果您想要使用计数器算法的话，只需要增加一个配置即可。在配置文件中指定算法为计数器算法。（推荐您使用yml文件）
    1. yml配置方式
    ```yaml
    current-limit:
      algorithm: counter
    ```
    2. properties配置方式
    ```properties
    current-limit.algorithm = counter
    ```

#### 再次开发
> 如果您想使用别的算法，您可以[fork项目进行开发](https://github.com/chqiuu/spring-redis-current-limit)

#### 作者信息
1. [个人博客](https://blog.csdn.net/QIU176161650)
2. [GitHub](https://github.com/chqiuu)

#### 版本信息

#####  1.0.0
>spring-redis-current-limit 正式上线
