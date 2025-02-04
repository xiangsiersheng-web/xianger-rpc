package com.ws.rpc.core.protection.ratelimit.nolimit;

import com.ws.rpc.core.protection.ratelimit.RateLimiter;

/**
 * 无限流
 * @author ws
 * @version 1.0
 * @date 2025-02-04 12:31
 */
public class NoRateLimiter implements RateLimiter {
    @Override
    public boolean allowRequest() {
        return true;
    }
}
