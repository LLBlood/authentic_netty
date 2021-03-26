package cn.liulin.authentic_netty.nio;

/**
 * cn.liulin.authentic_netty.nio$
 * 异步Nio服务端
 * @author ll
 * @date 2021-03-26 11:11:57
 **/
public class NioTimeServer {
    public static void main(String[] args) {
        int port = 8080;
        MultiplexerTimeServer multiplexerTimeServer = new MultiplexerTimeServer(port);
        new Thread(multiplexerTimeServer, "NIO-multiplexerTimeServer-001").start();
    }
}
