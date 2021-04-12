package cn.liulin.authentic_netty.aio;

/**
 * cn.liulin.authentic_netty.aio$
 *
 * @author ll
 * @date 2021-04-12 14:26:23
 **/
public class AioTimeServer {
    public static void main(String[] args) {
        int port = 8080;
        AsyncTimeServerHandler asyncTimeServerHandler = new AsyncTimeServerHandler(port);
        new Thread(asyncTimeServerHandler, "AIO-AsyncTimeServerHandler-001").start();
    }
}
