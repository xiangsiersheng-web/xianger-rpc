package com.ws.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 默认配置表示，熔断器默认开启，并且失败阈值为5，成功阈值为2，超时时间为10s，半开请求数最大为3
 * @author ws
 * @version 1.0
 * @date 2025-02-03 19:05
 */
@Data
@ConfigurationProperties(prefix = "rpc.client.circuit-breaker")
public class CircuitBreakerProperties {
    private boolean enabled;
    private int failureThreshold;
    private int successThreshold;
    private long timeout;
    private int maxHalfOpenRequests;

    public CircuitBreakerProperties() {
        this.enabled = true;
        this.failureThreshold = 5;
        this.successThreshold = 2;
        this.timeout = 10_000;
        this.maxHalfOpenRequests = 3;
    }
}
