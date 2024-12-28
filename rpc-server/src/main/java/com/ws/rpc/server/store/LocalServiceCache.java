package com.ws.rpc.server.store;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 4:26
 */

@Slf4j
public class LocalServiceCache {
    private static final Map<String, Object> serviceCache = new ConcurrentHashMap<>();

    public static void putService(String serviceKey, Object service) {
        serviceCache.put(serviceKey, service);
        log.debug("Put service: {} into cache.", serviceKey);
    }

    public static Object getService(String serviceKey) {
        return serviceCache.get(serviceKey);
    }

    public static void removeService(String serviceKey) {
        serviceCache.remove(serviceKey);
        log.debug("Remove service: {} from cache.", serviceKey);
    }
}
