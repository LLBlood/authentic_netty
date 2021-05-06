package cn.liulin.authentic_netty.netty.fixedlength;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * cn.liulin.authentic_netty.netty.fixedlength$
 *
 * @author ll
 * @date 2021-05-06 10:52:32
 **/
public class FixedEchoServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(String.format("Receive client : [%s]", msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
