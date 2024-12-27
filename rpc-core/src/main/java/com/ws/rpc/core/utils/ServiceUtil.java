package com.ws.rpc.core.utils;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 22:55
 */
public class ServiceUtil {
    public static String getServiceKey(String serviceName, String version) {
        return serviceName + ":" + version;
    }
}
