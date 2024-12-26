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

    public static void putService(String serviceName, Object service) {
        serviceCache.put(serviceName, service);
        log.debug("Put service: {} into cache.", serviceName);
    }

    public static Object getService(String serviceName) {
        return serviceCache.get(serviceName);
    }

    public static void removeService(String serviceName) {
        serviceCache.remove(serviceName);
        log.debug("Remove service: {} from cache.", serviceName);
    }
}
