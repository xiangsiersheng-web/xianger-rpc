package com.ws.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 22:33
 */
@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {
    private long timeout;
}
