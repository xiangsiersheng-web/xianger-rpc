package com.ws.rpc.client.annotation;


import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RpcReference {
    /**
     * 服务版本
     */
    String version() default "";

    int timeout() default 0;

    int retry() default 0;

    // 降级策略
    Class<?> fallback() default Void.class;
}
