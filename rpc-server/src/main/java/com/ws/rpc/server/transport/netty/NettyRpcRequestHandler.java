package com.ws.rpc.server.transport.netty;

import com.ws.rpc.core.constants.RpcConstants;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.enums.MessageType;
import com.ws.rpc.core.factory.SingletonFactory;
import com.ws.rpc.core.protocol.MessageHeader;
import com.ws.rpc.core.protocol.RpcMessage;
import com.ws.rpc.server.handler.RpcRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-02 1:10
 */
@Slf4j
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<RpcMessage> {
    private final RpcRequestHandler requestHandler;

    public NettyRpcRequestHandler() {
        this.requestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    /**
     * 响应请求
     * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *                      belongs to
     * @param msg           the message to handle
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            MessageType msgType = MessageType.valueOf(msg.getHeader().getMessageType());
            MessageHeader header = msg.getHeader();
            RpcMessage responseRpcMessage = new RpcMessage();
            if (msgType == MessageType.HEARTBEAT_REQUEST) {
                log.debug("heartbeat msg. {}", msg.getBody());
                header.setMessageType(MessageType.HEARTBEAT_RESPONSE.getCode());
                responseRpcMessage.setHeader(header);
                responseRpcMessage.setBody(RpcConstants.PONG);
            } else if (msgType == MessageType.REQUEST) {
                header.setMessageType(MessageType.RESPONSE.getCode());
                // 反射调用
                RpcRequest rpcRequest = (RpcRequest) msg.getBody();
                log.debug("The server received the request {}.", rpcRequest);
                RpcResponse rpcResponse = new RpcResponse();
                try {
                    Object result = requestHandler.handleRpcRequest(rpcRequest);
                    rpcResponse.setResult(result);
                } catch (Exception e) {
                    log.error("RpcRequestHandler handle error: {}", e.getMessage());
                    rpcResponse.setException(e);
                }
                responseRpcMessage.setHeader(header);
                responseRpcMessage.setBody(rpcResponse);
            }
            log.debug("The server will send msg. [{}]", responseRpcMessage);
            ctx.channel().writeAndFlush(responseRpcMessage);
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * 读空闲时，断开连接
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                log.warn("read idle happen [{}]", ctx.channel().remoteAddress());
                ctx.channel().close();
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
