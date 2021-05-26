package cn.liulin.authentic_netty.netty.msgpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * cn.liulin.authentic_netty.netty.msgpack$
 *
 * @author ll
 * @date 2021-05-24 14:13:40
 **/
public class MsgPackClient {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        String host = "127.0.0.1";
        new MsgPackClient().connect(port, host);
    }

    public void connect(int port, String host) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //首先加入LengthFieldBasedFrameDecoder解码器，解决tcp粘包和拆包的问题
                            ch.pipeline().addLast("frameDecoder",
                                    new LengthFieldBasedFrameDecoder(65535, 0,2,0,2));
                            ch.pipeline().addLast("msgpack decoder", new MsgpackDecoder());
                            //加入LengthFieldPrepender编码器，解决tcp粘包和拆包的问题
                            ch.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
                            ch.pipeline().addLast("msgpack encoder", new MsgpackEncoder());
                            ch.pipeline().addLast(new MsgPackClientHandler(100));
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            //如果监听到关闭事件，可以优雅的关闭通道和nettyserver
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
