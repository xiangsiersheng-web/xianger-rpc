package com.ws.rpc.client.proxy;

import com.ws.rpc.client.remotecall.RemoteMethodCall;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ClientStubCglibProxy implements MethodInterceptor {
    private final String serviceKey;
    private final RemoteMethodCall remoteMethodCall;
    private final int timeout;
    private final int retry;
    private final Class<?> fallback;

    public ClientStubCglibProxy(String serviceKey, RemoteMethodCall remoteMethodCall) {
        this(serviceKey, remoteMethodCall, 0, 0, Void.class);
    }

    public ClientStubCglibProxy(String serviceKey, RemoteMethodCall remoteMethodCall, int timeout, int retry) {
        this(serviceKey, remoteMethodCall, timeout, retry, Void.class);
    }

    public ClientStubCglibProxy(String serviceKey, RemoteMethodCall remoteMethodCall, int timeout, int retry, Class<?> fallback) {
        this.serviceKey = serviceKey;
        this.remoteMethodCall = remoteMethodCall;
        this.timeout = timeout;
        this.retry = retry;
        this.fallback = fallback;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        try {
            return remoteMethodCall.call(serviceKey, method, args, timeout, retry);
        } catch (Exception e) {
            if (fallback != Void.class) {
                log.info("RPC call failed, will following fallback. Method : {}, args : {}",
                        method, args, e);
                Object obj = fallback.getDeclaredConstructor().newInstance();
                return method.invoke(obj, args);
            }
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }
}
