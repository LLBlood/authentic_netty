package cn.liulin.authentic_netty.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * cn.liulin.authentic_netty.netty.simple$
 *
 * @author ll
 * @date 2021-04-13 09:36:03
 **/
public class NettyTimeClient {
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
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyTimeClientHandler());
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
        new NettyTimeClient().connect(port, "127.0.0.1");
    }
}
