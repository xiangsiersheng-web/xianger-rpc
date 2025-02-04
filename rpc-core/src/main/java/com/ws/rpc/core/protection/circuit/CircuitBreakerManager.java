package com.ws.rpc.core.protection.circuit;

import com.ws.rpc.core.config.CircuitBreakerProperties;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-03 16:49
 */
public class CircuitBreakerManager {
    private final static ConcurrentHashMap<String, CircuitBreaker> breakers = new ConcurrentHashMap<>();

    public static CircuitBreaker getBreaker(String breakerKey) {
        return getBreaker(breakerKey, 5, 3, 2, 10_000);
    }

    public static CircuitBreaker getBreaker(String breakerKey, CircuitBreakerProperties properties) {
        return getBreaker(breakerKey, properties.getFailureThreshold(), properties.getSuccessThreshold(), properties.getMaxHalfOpenRequests(), properties.getTimeout());
    }

    public static CircuitBreaker getBreaker(String breakerKey, int failureThreshold, int successThreshold, int maxHalfOpenRequests, long timeout) {
        return breakers.computeIfAbsent(breakerKey, key -> new CircuitBreaker(failureThreshold, successThreshold, maxHalfOpenRequests, timeout));
    }
}
