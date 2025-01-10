package com.ws.rpc.core.enums;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-10 22:44
 */
public class RetryStrategyTypeTest {

    @Test
    public void testRetryStrategy() {
        RetryStrategyType retryStrategy1 = RetryStrategyType.NO_RETRY;
        RetryStrategyType noRetry = RetryStrategyType.fromString("noRetry");
        assertEquals(retryStrategy1, noRetry);
    }

}