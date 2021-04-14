package cn.liulin.authentic_netty.netty.simple;

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
public class NettyTimeServerHandler extends ChannelHandlerAdapter {
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
        String body = new String(req, "UTF-8");
        System.out.println("The time server receive order : " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                : "BAD QUERY";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        /**
         * 异步发送应答消息给客户端
         */
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * 将消息发送队列中的消息写入到SocketChannel中发送给对方
         * Netty的write方法并不直接将消息写入SocketChannel中，调用write方法只是把待发送的消息放到发送缓冲数组中，
         * 再调用flush方法，将发送缓冲区的消息全部写道SocketChannel中
         */
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
