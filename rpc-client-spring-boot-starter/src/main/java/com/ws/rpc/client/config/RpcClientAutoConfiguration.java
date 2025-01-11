package com.ws.rpc.client.config;

import com.ws.rpc.client.proxy.ClientStubProxyFactory;
import com.ws.rpc.client.remotecall.RemoteMethodCall;
import com.ws.rpc.client.spring.RpcClientBeanPostProcessor;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.client.transport.netty.NettyRpcClient;
import com.ws.rpc.client.transport.socket.SocketRpcClient;
import com.ws.rpc.core.enums.RetryStrategyType;
import com.ws.rpc.core.fault.retry.RetryStrategy;
import com.ws.rpc.core.fault.retry.RetryStrategyFactory;
import com.ws.rpc.core.loadbalance.LoadBalance;
import com.ws.rpc.core.loadbalance.impl.ConsistentHashLoadBalance;
import com.ws.rpc.core.loadbalance.impl.RandomLoadBalance;
import com.ws.rpc.core.loadbalance.impl.RoundRobinLoadBalance;
import com.ws.rpc.core.registry.ServiceDiscovery;
import com.ws.rpc.core.registry.zookeeper.ZookeeperServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
    public LoadBalance loadBalance(@Value("${rpc.client.loadBalance:roundRobin}") String loadBalanceType) {
        return switch (loadBalanceType) {
            case "random" -> new RandomLoadBalance();
            case "roundRobin" -> new RoundRobinLoadBalance();
            case "consistentHash" -> new ConsistentHashLoadBalance();
            default -> throw new IllegalArgumentException("unknown load balance type: " + loadBalanceType);
        };
    }

    @Bean(name = "serviceDiscovery")
    @ConditionalOnBean(LoadBalance.class)
    public ServiceDiscovery serviceDiscovery(@Value("${rpc.client.registry:zookeeper}") String registryType,
                                             @Autowired LoadBalance loadBalance) {
        switch (registryType) {
            case "zookeeper":
                log.debug("Creating zookeeper service discovery instance. connect {}", properties.getRegistryAddr());
                return new ZookeeperServiceDiscovery(properties.getRegistryAddr(), loadBalance);
            default:
                throw new IllegalArgumentException("Unsupported registry type: " + registryType);
        }
    }

    @Bean(name = "rpcClient")
    public RpcClient rpcClient(@Value("${rpc.client.transport:netty}") String transportType) {
        return switch (transportType) {
            case "netty" -> new NettyRpcClient();
            case "socket" -> new SocketRpcClient();
            default -> throw new IllegalArgumentException("Unsupported transport type: " + transportType);
        };
    }


    @Bean(name = "remoteMethodCall")
    @ConditionalOnBean({RpcClient.class, ServiceDiscovery.class})
    public RemoteMethodCall remoteMethodCall(@Autowired RpcClient rpcClient,
                                             @Autowired ServiceDiscovery serviceDiscovery) {
        // RemoteMethodCall 负责远程方法的调用
        log.debug("Creating remote method call instance. dependency: {} \n {}", rpcClient, serviceDiscovery);
        return new RemoteMethodCall(properties, rpcClient, serviceDiscovery);
    }

    @Bean(name = "clientStubProxyFactory")
    @ConditionalOnBean(RemoteMethodCall.class)
    public ClientStubProxyFactory clientStubProxyFactory(@Autowired RemoteMethodCall remoteMethodCall) {
        // 代理工厂需要拿到RemoteMethodCall，注入到代理对象中，这样代理对象才能调用远程方法
        return new ClientStubProxyFactory(remoteMethodCall);
    }

    @Bean(name = "rpcClientBeanPostProcessor")
    @ConditionalOnBean(ClientStubProxyFactory.class)
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(@Autowired ClientStubProxyFactory clientStubProxyFactory) {
        // RpcClientBeanPostProcessor负责扫描被@RpcReference注解的类，生成代理对象
        return new RpcClientBeanPostProcessor(clientStubProxyFactory);
    }
}
