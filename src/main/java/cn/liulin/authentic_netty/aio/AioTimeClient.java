package cn.liulin.authentic_netty.aio;

/**
 * cn.liulin.authentic_netty.aio$
 *
 * @author ll
 * @date 2021-04-12 15:16:56
 **/
public class AioTimeClient {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new AsyncTimeClientHandler("127.0.0.1", port), "AIO-AsyncTimeClientHandler-001").start();
    }
}
