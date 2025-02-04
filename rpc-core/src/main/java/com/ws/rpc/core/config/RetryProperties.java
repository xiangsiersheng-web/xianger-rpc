package com.ws.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-03 18:55
 */
@Data
@ConfigurationProperties(prefix = "rpc.client.retry")
public class RetryProperties {
    private String retryStrategy;
    private int retryMax;
    private long retryInterval;

    public RetryProperties() {
        this.retryStrategy = "exponential_backoff";
        this.retryMax = 3;
        this.retryInterval = 1000;
    }
}
