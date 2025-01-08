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
}
