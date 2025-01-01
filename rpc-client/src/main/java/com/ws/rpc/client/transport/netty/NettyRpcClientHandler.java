package com.ws.rpc.client.transport.netty;

import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.enums.MessageType;
import com.ws.rpc.core.protocol.RpcMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 21:42
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            MessageType msgType = MessageType.valueOf(msg.getHeader().getMessageType());
            if (msgType == MessageType.RESPONSE) {
                // 得到响应
                int messageId = msg.getHeader().getMessageId();
                Promise<RpcMessage> promise = UnprocessedRequests.remove(messageId);
                if (promise != null) {
                    RpcResponse rpcResponse = (RpcResponse) msg.getBody();
                    Exception exception = rpcResponse.getException();
                    if (exception == null) {
                        promise.setSuccess(msg);
                    } else {
                        promise.setFailure(exception);
                    }
                } else if (msgType == MessageType.HEARTBEAT_RESPONSE){
                    log.info("Heartbeat msg. {}", msg.getBody());
                }
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 写空闲时 发送心跳
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                log.info("Write idle happen [{}]", ctx.channel().remoteAddress());
                // 心跳包
                RpcMessage rpcMessage = RpcMessage.getPing();
                ctx.channel().writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
