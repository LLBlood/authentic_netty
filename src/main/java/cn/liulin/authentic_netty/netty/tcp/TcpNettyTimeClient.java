package cn.liulin.authentic_netty.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * cn.liulin.authentic_netty.netty.simple$
 *
 * @author ll
 * @date 2021-04-13 09:36:03
 **/
public class TcpNettyTimeClient {
    public void connect(int port, String host) throws Exception {
        /**
         * 配置客户端NIO线程组
         * 客户端处理I/O读写的NioEventLoopGroup线程组
         */
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            /**
             * 客户端辅助启动类Bootstrap
             */
            Bootstrap b = new Bootstrap();
            b.group(group)
                    /**
                     * channel设置为NioSocketChannel
                     */
                    .channel(NioSocketChannel.class)
                    /**
                     * 提高数据的实时性
                     */
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TcpNettyTimeClientHandler());
                        }
                    });

            //发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();

            //等待客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new TcpNettyTimeClient().connect(port, "127.0.0.1");
    }
}
