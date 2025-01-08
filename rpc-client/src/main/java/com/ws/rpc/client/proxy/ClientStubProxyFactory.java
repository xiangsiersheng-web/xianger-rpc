package com.ws.rpc.client.proxy;

import com.ws.rpc.client.annotation.RpcReference;
import com.ws.rpc.client.remotecall.RemoteMethodCall;
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
    private final RemoteMethodCall remoteMethodCall;
    public ClientStubProxyFactory(RemoteMethodCall remoteMethodCall) {
        this.remoteMethodCall = remoteMethodCall;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz, RpcReference rpcReference) {
        int timeout = rpcReference.timeout();
        int retry = rpcReference.retry();
        // clazz: com.ws.rpc.service.HelloService
        return (T) proxyCache.computeIfAbsent(ServiceUtil.getServiceKey(clazz.getName(), rpcReference.version()), key -> {
            if (clazz.isInterface()) {
                // 优先使用 JDK 动态代理
                return new ClientStubJDKProxy(key, remoteMethodCall, timeout, retry).getProxyInstance(clazz);
            } else {
                // 如果不是接口，则使用 CGLIB 动态代理
                return new ClientStubCglibProxy(key, remoteMethodCall, timeout, retry).getProxyInstance(clazz);
            }
        });
    }
}
