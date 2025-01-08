package com.ws.rpc.client.proxy;

import com.ws.rpc.client.remotecall.RemoteMethodCall;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 23:30
 */
public class ClientStubJDKProxy implements InvocationHandler {
    private final String serviceKey;
    private final RemoteMethodCall remoteMethodCall;
    private final int timeout;
    private final int retry;

    public ClientStubJDKProxy(String serviceKey, RemoteMethodCall remoteMethodCall) {
        this(serviceKey, remoteMethodCall, 0, 0);
    }

    public ClientStubJDKProxy(String serviceKey, RemoteMethodCall remoteMethodCall, int timeout, int retry) {
        this.serviceKey = serviceKey;
        this.remoteMethodCall = remoteMethodCall;
        this.timeout = timeout;
        this.retry = retry;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                                    new Class[]{clazz},
                                    this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return remoteMethodCall.call(serviceKey, method, args, timeout, retry);
    }
}
