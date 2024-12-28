package com.ws.rpc.server.annotation;

import com.ws.rpc.server.spring.RpcRegistry;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @description: 指定要扫描的路径
 * @author ws
 * @version 1.0
 * @date 2024-12-28 1:09
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Import(RpcRegistry.class)
public @interface RpcScan {
    String[] basePackages();
}
