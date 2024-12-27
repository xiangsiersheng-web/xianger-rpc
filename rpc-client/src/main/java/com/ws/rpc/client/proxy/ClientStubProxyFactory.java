package com.ws.rpc.client.proxy;

import com.ws.rpc.core.utils.ServiceUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 23:30
 */
public class ClientStubProxyFactory {

    private static final Map<String, Object> proxyCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz, String version) {
        return (T) proxyCache.computeIfAbsent(ServiceUtil.getServiceKey(clazz.getName(), version), key -> {
            if (clazz.isInterface()) {
                // 优先使用 JDK 动态代理
                return new ClientStubJDKProxy(key).getProxyInstance(clazz);
            } else {
                // 如果不是接口，则使用 CGLIB 动态代理
                return new ClientStubCglibProxy(key).getProxyInstance(clazz);
            }
        });
    }
}
