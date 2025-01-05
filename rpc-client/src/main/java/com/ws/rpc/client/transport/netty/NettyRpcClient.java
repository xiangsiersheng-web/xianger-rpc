package com.ws.rpc.client.transport.netty;

import com.ws.rpc.client.dto.RpcRequestMetaData;
import com.ws.rpc.client.transport.RpcClient;
import com.ws.rpc.core.codec.RpcFrameDecoder;
import com.ws.rpc.core.codec.RpcMessageCodec;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.protocol.RpcMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 20:32
 */
@Slf4j
public class NettyRpcClient implements RpcClient {
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ChannelProvider channelProvider;
    private static final RpcFrameDecoder RPC_FRAME_DECODER = new RpcFrameDecoder();
    private static final RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();

    public NettyRpcClient() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 写空闲时触发一个 IdleStateEvent#WRITER_IDLE
                        ch.pipeline().addLast(new IdleStateHandler(0, 15, 0, TimeUnit.SECONDS));
                        // 帧解码器 粘包拆包
                        ch.pipeline().addLast(RPC_FRAME_DECODER);
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        // 协议编解码器
                        ch.pipeline().addLast(RPC_MESSAGE_CODEC);
                        // 响应消息处理器
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }


    @Override
    @SneakyThrows
    public RpcResponse sendRequest(RpcRequestMetaData rpcRequestMetaData) {
        // 根据ip和port获取channel
        Channel channel = getChannel(rpcRequestMetaData.getServiceAddress(), rpcRequestMetaData.getServicePort());
        if (channel != null && channel.isActive()) {
            // 完成后由channel.eventLoop()进行通知
            Promise<RpcMessage> promise = new DefaultPromise<>(channel.eventLoop());
            int requestId = rpcRequestMetaData.getRpcMessage().getHeader().getMessageId();
            UnprocessedRequests.put(requestId, promise);
            // 发送数据
            channel.writeAndFlush(rpcRequestMetaData.getRpcMessage()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.debug("The client send successfully. request: {}", rpcRequestMetaData);
                    } else {
                        promise.setFailure(future.cause());
                        log.error("The client send the message failed.", future.cause());
                    }
                }
            });
            // 超时设置
            Long timeout = rpcRequestMetaData.getTimeout();
            if (timeout != null && timeout > 0) {
                boolean isSuccess = promise.await(timeout, TimeUnit.MILLISECONDS);
                if (!isSuccess) {
                    log.error("The client send the message timeout. {}", rpcRequestMetaData);
                }
            } else {
                promise.await();
            }
            if (promise.isSuccess()) {
                return (RpcResponse) promise.get().getBody();
            } else {
                throw new RpcException(promise.cause());
            }
        } else {
            throw new RpcException("The channel is null or not active.");
        }
    }

    private Channel getChannel(String serviceAddress, Integer servicePort) {
        Channel channel = channelProvider.get(serviceAddress, servicePort);
        if (channel == null) {
            channel = doConnect(serviceAddress, servicePort);
            channelProvider.set(serviceAddress, servicePort, channel);
        }
        return channel;
    }

    private Channel doConnect(String serviceAddress, Integer servicePort) {
        log.info("Connecting to remote service address: {}:{}", serviceAddress, servicePort);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(serviceAddress, servicePort);
        CompletableFuture<Channel> channelFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("The client has connected to the remote service address {}:{}", serviceAddress, servicePort);
                    channelFuture.complete(future.channel());
                } else {
                    log.error("Failed to connect to the remote service address {}:{}", serviceAddress, servicePort);
                    channelFuture.completeExceptionally(future.cause());
                }
            }
        });

        Channel channel = null;
        try {
            channel = channelFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
