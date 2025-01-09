package com.ws.rpc.server.spring;

import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.registry.ServiceRegistry;
import com.ws.rpc.core.utils.ServiceUtil;
import com.ws.rpc.server.annotation.RpcService;
import com.ws.rpc.server.config.RpcServerProperties;
import com.ws.rpc.server.store.LocalServiceCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;

/**
 * 这个生效的前提是被注册成了bean
 * @author ws
 * @version 1.0
 * @date 2024-12-28 11:28
 */

@Slf4j
public class RpcBeanPostProcessor implements BeanPostProcessor {
    private final RpcServerProperties rpcServerProperties;
    private final ServiceRegistry serviceRegistry;
    public RpcBeanPostProcessor(RpcServerProperties rpcServerProperties, ServiceRegistry serviceRegistry) {
        this.rpcServerProperties = rpcServerProperties;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with [{}].", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // 获取 @RpcService 注解实例
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);

            // 应该获取类实现接口的路径名
            String interfaceName = rpcService.interfaceClass().getName(); // eg. com.ws.rpc.service.HelloService
//            String className = bean.getClass().getName(); // eg. com.ws.rpc.service.HelloServiceImpl
            String serviceKey = ServiceUtil.getServiceKey(interfaceName, rpcService.version());
            try {
                ServiceInfo serviceInfo = ServiceInfo.builder()
                                        .serviceKey(serviceKey)
                                        .version(rpcService.version())
                                        .host(InetAddress.getLocalHost().getHostAddress())
                                        .port(rpcServerProperties.getPort())
                                        .providerName(rpcServerProperties.getProviderName())
                                        .build();

                // 注册
                serviceRegistry.register(serviceInfo);
                LocalServiceCache.putService(serviceKey, bean);
            } catch (Exception e) {
                log.error("An error occurred while registering service [%s] at address [localhost:%d]",
                        serviceKey, rpcServerProperties.getPort());
                throw new RpcException(e);
            }
        }
        return bean;
    }
}
