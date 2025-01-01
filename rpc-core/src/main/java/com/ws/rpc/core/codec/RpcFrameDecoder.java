package com.ws.rpc.core.codec;

import com.ws.rpc.core.constants.RpcConstants;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 解决粘包、半包问题
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:41
 */
@ChannelHandler.Sharable
public class RpcFrameDecoder extends LengthFieldBasedFrameDecoder {
    public RpcFrameDecoder() {
        // lengthFieldOffset：长度字段的偏移量，从 com.ws.rpc.core.protocol.MessageHeader，也即协议的定义计算得出
        // lengthFieldLength：长度字段的长度，也即协议中定义的长度字段所占的字节数
        // lengthAdjustment：长度字段 adjustment，因为length表示全文长度，所以只需要再读取 length-16 即可
        // initialBytesToStrip：初始需要跳过的字节数，不跳过，要获取完成的RpcMessage
        this(RpcConstants.MAX_FRAME_LENGTH, 12, 4, -16, 0);
    }

    public RpcFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
