package com.ws.rpc.core.enums;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:22
 */
@Slf4j
public enum RetryStrategyType {
    FIXED_INTERVAL,
    EXPONENTIAL_BACKOFF,
    INFINITE,
    NO_RETRY;


    public static RetryStrategyType fromString(String name) {
        for (RetryStrategyType type : RetryStrategyType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        log.warn("Invalid retry strategy type: " + name);
        return NO_RETRY;
    }
}
