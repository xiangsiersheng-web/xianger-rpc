package com.ws.rpc.client.proxy;

import com.ws.rpc.client.remotecall.RemoteMethodCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 23:30
 */
@Slf4j
public class ClientStubJDKProxy implements InvocationHandler {
    private final String serviceKey;
    private final RemoteMethodCall remoteMethodCall;
    private final int timeout;
    private final int retry;
    private final Class<?> fallback;

    public ClientStubJDKProxy(String serviceKey, RemoteMethodCall remoteMethodCall) {
        this(serviceKey, remoteMethodCall, 0, 0, Void.class);
    }

    public ClientStubJDKProxy(String serviceKey, RemoteMethodCall remoteMethodCall, int timeout, int retry) {
        this(serviceKey, remoteMethodCall, timeout, retry, Void.class);
    }

    public ClientStubJDKProxy(String key, RemoteMethodCall remoteMethodCall, int timeout, int retry, Class<?> fallback) {
        this.serviceKey = key;
        this.remoteMethodCall = remoteMethodCall;
        this.timeout = timeout;
        this.retry = retry;
        this.fallback = fallback;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                                    new Class[]{clazz},
                                    this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return remoteMethodCall.call(serviceKey, method, args, timeout, retry);
        } catch (Exception e) {
            if (fallback != Void.class) {
                log.error("RPC call failed, will following fallback. Method : {}, args : {}",
                        method, args, e);
                Object obj = fallback.getDeclaredConstructor().newInstance();
                return method.invoke(obj, args);
            }
            throw e;
        }
    }
}
