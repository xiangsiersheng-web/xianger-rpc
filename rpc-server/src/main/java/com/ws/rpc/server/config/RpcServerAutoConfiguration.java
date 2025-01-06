package com.ws.rpc.server.config;

import com.ws.rpc.core.registry.ServiceRegistry;
import com.ws.rpc.core.registry.zk.ZkServiceRegistry;
import com.ws.rpc.core.registry.zookeeper.ZookeeperServiceRegistry;
import com.ws.rpc.server.spring.RpcBeanPostProcessor;
import com.ws.rpc.server.spring.RpcServerRunner;
import com.ws.rpc.server.transport.RpcServer;
import com.ws.rpc.server.transport.netty.NettyRpcServer;
import com.ws.rpc.server.transport.socket.SocketRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 20:51
 */
@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcServerAutoConfiguration {
    private final RpcServerProperties properties;

    public RpcServerAutoConfiguration(RpcServerProperties rpcServerProperties) {
        this.properties = rpcServerProperties;
    }

    @Bean
    public ServiceRegistry zkServiceRegistry() {
//        return new ZkServiceRegistry(properties.getRegistryAddr());
        return new ZookeeperServiceRegistry(properties.getRegistryAddr());
    }

//    @Bean
//    public RpcServer rpcServer() {
//        return new SocketRpcServer();
//    }

    @Bean
    public RpcServer rpcServer() {
        return new NettyRpcServer();
    }

    @Bean
    public RpcBeanPostProcessor rpcBeanPostProcessor(@Autowired RpcServerProperties properties,
                                                     @Autowired ServiceRegistry serviceRegistry) {
        return new RpcBeanPostProcessor(properties, serviceRegistry);
    }

    @Bean
    public RpcServerRunner rpcServerRunner(@Autowired RpcServer rpcServer,
                                           @Autowired RpcServerProperties properties,
                                           @Autowired ServiceRegistry serviceRegistry) {
        return new RpcServerRunner(rpcServer, properties, serviceRegistry);
    }
}
