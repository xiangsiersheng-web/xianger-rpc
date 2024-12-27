package com.ws.rpc.client.spring;

import com.ws.rpc.client.annotation.RpcReference;
import com.ws.rpc.client.proxy.ClientStubProxyFactory;
import com.ws.rpc.core.exception.RpcException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 23:44
 */
public class RpcClientBeanPostProcessor implements BeanPostProcessor {


    /**
     * 对象初始化后，对注解进行扫描，生成代理对象
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取类的所有属性
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        // 检查属性
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                Class<?> clazz = declaredField.getType();
                Object proxy = ClientStubProxyFactory.getProxy(clazz, rpcReference.version());
                try {
                    declaredField.setAccessible(true);
                    declaredField.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RpcException(String.format("Failed to set proxy. Field: %s, Type: %s",
                            declaredField.getName(), clazz.getName()), e);
                }
            }
        }
        return bean;
    }
}
