package com.ws.rpc.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 实现Spring自动配置
 * @author ws
 * @version 1.0
 * @date 2024-12-27 22:38
 */
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class SpringClientAutoConfiguration {
    private final RpcClientProperties rpcClientProperties;
    public SpringClientAutoConfiguration(RpcClientProperties rpcClientProperties) {
        this.rpcClientProperties = rpcClientProperties;
    }
}
