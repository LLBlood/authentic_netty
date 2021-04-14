package cn.liulin.authentic_netty.netty.halftcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * cn.liulin.authentic_netty.netty.simple$
 *
 * @author ll
 * @date 2021-04-12 16:33:48
 **/
public class HalfNettyTimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * ByteBuf类似于JDK中的java.nio.ByteBuffer对象
         */
        ByteBuf buf = (ByteBuf) msg;
        /**
         * 通过ByteBuf的readableBytes方法可以获取缓冲区可读的字节数
         */
        byte[] req = new byte[buf.readableBytes()];
        /**
         * 通过ByteBuf的readBytes方法将缓冲区中的字节数组复制到新建的byte数组中
         */
        buf.readBytes(req);
        String body = new String(req, "UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
        System.out.println("The time server receive order : " + body + "; the counter is : " + ++counter);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                : "BAD QUERY";
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        /**
         * 异步发送应答消息给客户端
         */
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
