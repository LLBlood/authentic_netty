package cn.liulin.authentic_netty.nio;

/**
 * cn.liulin.authentic_netty.nio$
 * nio客户端
 * @author ll
 * @date 2021-03-26 16:28:18
 **/
public class NioTimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new TimeClientHandler("127.0.0.1", port), "TimeClient-001").start();
    }
}
