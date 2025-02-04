package com.ws.rpc.core.protection.ratelimit;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-04 10:44
 */
public interface RateLimiter {
    boolean allowRequest();
}
