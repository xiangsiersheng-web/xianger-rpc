package com.ws.rpc.core.codec;

import com.ws.rpc.core.compression.Compression;
import com.ws.rpc.core.compression.CompressionFactory;
import com.ws.rpc.core.protocol.ProtocolConstants;
import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.RpcResponse;
import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.enums.MessageType;
import com.ws.rpc.core.enums.SerializationType;
import com.ws.rpc.core.exception.RpcException;
import com.ws.rpc.core.protocol.MessageHeader;
import com.ws.rpc.core.protocol.RpcMessage;
import com.ws.rpc.core.serialization.Serialization;
import com.ws.rpc.core.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.Arrays;
import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-01 22:55
 */
@ChannelHandler.Sharable
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {

    /**
     * 将RpcMessage 编码为ByteBuf
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        MessageHeader header = msg.getHeader();
        buffer.writeBytes(header.getMagicNumber()); // 魔数
        buffer.writeByte(header.getVersion());      // 版本号
        buffer.writeByte(header.getMessageType());  // 消息类型
        buffer.writeByte(header.getSerializationAlgorithm());   // 序列化算法
        buffer.writeByte(header.getCompressionAlgorithm());     // 压缩算法
        buffer.writeInt(header.getMessageId());                 // 消息id

        int fullLength = ProtocolConstants.HEAD_LENGTH;
        byte[] bodyBytes = null;
        if (msg.getBody() != null) {
            Serialization serialization = SerializationFactory.getSerialization(
                    SerializationType.valueOf(header.getSerializationAlgorithm()));
            bodyBytes = serialization.serialize(msg.getBody());
            // todo 压缩
            Compression compression = CompressionFactory.getCompression(CompressionType.valueOf(header.getCompressionAlgorithm()));
            bodyBytes = compression.compress(bodyBytes);
            fullLength += bodyBytes.length;
        }
        buffer.writeInt(fullLength);
        if (bodyBytes != null) {
            buffer.writeBytes(bodyBytes);
        }
        out.add(buffer);
    }

    /**
     * 将ByteBuf 解码为RpcMessage
     * @param ctx：通信上下文
     * @param msg：待解码的消息
     * @param out：解码后的消息
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] magicNumber = checkMagicNumber(msg);
        byte version = checkVersion(msg);
        byte messageType = msg.readByte();
        byte serializationAlgorithm = msg.readByte();
        byte compressionAlgorithm = msg.readByte();
        int messageId = msg.readInt();
        int fullLength = msg.readInt();

        MessageHeader header = MessageHeader.builder()
                .magicNumber(magicNumber)
                .version(version)
                .messageType(messageType)
                .serializationAlgorithm(serializationAlgorithm)
                .compressionAlgorithm(compressionAlgorithm)
                .messageId(messageId)
                .fullLength(fullLength)
                .build();
        RpcMessage rpcMessage = new RpcMessage(header, null);

        int bodyLength = fullLength - ProtocolConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] tmp = new byte[bodyLength];
            msg.readBytes(tmp);
            Compression compression = CompressionFactory.getCompression(
                    CompressionType.valueOf(compressionAlgorithm));
            tmp = compression.decompress(tmp);
            Serialization serialization = SerializationFactory.getSerialization(
                    SerializationType.valueOf(serializationAlgorithm));
            MessageType msgType = MessageType.valueOf(header.getMessageType());
            if (msgType == MessageType.REQUEST) {
                RpcRequest rpcRequest = serialization.deserialize(tmp, RpcRequest.class);
                rpcMessage.setBody(rpcRequest);
            } else if (msgType == MessageType.RESPONSE) {
                RpcResponse rpcResponse = serialization.deserialize(tmp, RpcResponse.class);
                rpcMessage.setBody(rpcResponse);
            } else if (msgType == MessageType.HEARTBEAT_REQUEST || msgType == MessageType.HEARTBEAT_RESPONSE) {
                String heartbeat = serialization.deserialize(tmp, String.class);
                rpcMessage.setBody(heartbeat);
            }
            out.add(rpcMessage);
        }
    }

    private byte[] checkMagicNumber(ByteBuf msg) {
        int len = ProtocolConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        msg.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != ProtocolConstants.MAGIC_NUMBER[i]) {
                throw new RpcException("Unknown magic code: " + Arrays.toString(tmp));
            }
        }
        return tmp;
    }

    private byte checkVersion(ByteBuf msg) {
        byte version = msg.readByte();
        if (version != ProtocolConstants.VERSION) {
            throw new RpcException("Version is not compatible: " + version);
        }
        return version;
    }
}
