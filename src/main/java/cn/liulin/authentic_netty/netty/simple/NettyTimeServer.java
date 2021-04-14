package cn.liulin.authentic_netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * cn.liulin.authentic_netty.netty.simple$
 *
 * @author ll
 * @date 2021-04-12 16:22:15
 **/
public class NettyTimeServer {
    public void bind(int port) throws Exception {
        /**
         * 配置服务端的NIO线程组，NioEventLoopGroup包含一组NIO线程，专门用于网络事件的处理，实际上就是Reactor线程组
         * 此处创建两个的原因是，一个用于服务端接受客户端的连接，另一个用于进行SocketChannel的网络读写
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * ServerBootstrap是Netty用于启动NIO服务端的辅助启动类，目的是降低服务端的开发复杂度。
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    /**
                     * 设置channel为NioServerSocketChannel，功能对应于JDK NIO类库中的ServerSocketChannel
                     */
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            //绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new NettyTimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new NettyTimeServer().bind(port);
    }
}
