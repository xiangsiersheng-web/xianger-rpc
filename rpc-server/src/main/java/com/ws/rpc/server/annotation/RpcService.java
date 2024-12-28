package com.ws.rpc.server.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcService {
    Class<?> interfaceClass() default void.class;
    String version() default "";
}
