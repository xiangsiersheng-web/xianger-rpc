package com.ws.rpc.client.config;

import com.ws.rpc.client.proxy.ClientStubProxyFactory;
import com.ws.rpc.client.remotecall.RemoteMethodCall;
import com.ws.rpc.client.spring.RpcClientBeanPostProcessor;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.client.transport.netty.NettyRpcClient;
import com.ws.rpc.client.transport.socket.SocketRpcClient;
import com.ws.rpc.core.loadbalance.LoadBalance;
import com.ws.rpc.core.loadbalance.impl.RoundRobinLoadBalance;
import com.ws.rpc.core.registry.ServiceDiscovery;
import com.ws.rpc.core.registry.zk.ZkServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现Spring自动配置
 * @author ws
 * @version 1.0
 * @date 2024-12-27 22:38
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
public class RpcClientAutoConfiguration {
    private final RpcClientProperties properties;
    public RpcClientAutoConfiguration(RpcClientProperties rpcClientProperties) {
        this.properties = rpcClientProperties;
    }

    @Bean(name = "loadBalance")
    public LoadBalance roundRobinLoadBalance() {
        return new RoundRobinLoadBalance();
    }

    @Bean(name = "serviceDiscovery")
    public ServiceDiscovery zkServiceDiscovery(@Autowired LoadBalance loadBalance) {
        log.debug("Creating service discovery instance. connect {}", properties.getRegistryAddr());
        return new ZkServiceDiscovery(properties.getRegistryAddr(), loadBalance);
    }

//    @Bean(name = "rpcClient")
//    public RpcClient rpcClient(@Autowired ServiceDiscovery serviceDiscovery) {
//        return new SocketRpcClient();
//    }

    @Bean(name = "rpcClient")
    public RpcClient rpcClient(@Autowired ServiceDiscovery serviceDiscovery) {
        return new NettyRpcClient();
    }

    @Bean
    public RemoteMethodCall remoteMethodCall(@Autowired RpcClient rpcClient,
                                             @Autowired ServiceDiscovery serviceDiscovery) {
        // RemoteMethodCall 负责远程方法的调用
        log.debug("Creating remote method call instance. dependency: {} \n {}", rpcClient, serviceDiscovery);
        return new RemoteMethodCall(properties, rpcClient, serviceDiscovery);
    }

    @Bean
    public ClientStubProxyFactory clientStubProxyFactory(@Autowired RemoteMethodCall remoteMethodCall) {
        // 代理工厂需要拿到RemoteMethodCall，注入到代理对象中，这样代理对象才能调用远程方法
        return new ClientStubProxyFactory(remoteMethodCall);
    }

    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory) {
        // RpcClientBeanPostProcessor负责扫描被@RpcReference注解的类，生成代理对象
        return new RpcClientBeanPostProcessor(clientStubProxyFactory);
    }
}
