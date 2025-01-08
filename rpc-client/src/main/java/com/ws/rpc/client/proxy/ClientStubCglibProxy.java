package com.ws.rpc.client.proxy;

import com.ws.rpc.client.remotecall.RemoteMethodCall;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 23:30
 */
public class ClientStubCglibProxy implements MethodInterceptor {
    private final String serviceKey;
    private final RemoteMethodCall remoteMethodCall;
    private final int timeout;
    private final int retry;

    public ClientStubCglibProxy(String serviceKey, RemoteMethodCall remoteMethodCall) {
        this(serviceKey, remoteMethodCall, 0, 0);
    }

    public ClientStubCglibProxy(String serviceKey, RemoteMethodCall remoteMethodCall, int timeout, int retry) {
        this.serviceKey = serviceKey;
        this.remoteMethodCall = remoteMethodCall;
        this.timeout = timeout;
        this.retry = retry;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return remoteMethodCall.call(serviceKey, method, args, timeout, retry);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }
}
