package com.ws.rpc.core.extension;

import java.lang.annotation.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-16 13:20
 */
@Documented // 显示在文档中
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时存在
@Target({ElementType.TYPE}) // 注解在类上
public @interface SPI {
}
