package cn.liulin.authentic_netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * cn.liulin.authentic_netty.nio$
 * 多路复用器，一个独立的线程，负责轮询多路复用器Selector，可以处理多个客户端的并发接入
 * @author ll
 * @date 2021-03-26 11:12:44
 **/
public class MultiplexerTimeServer implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听接口<br/>
     * 创建多路复用器Selector、ServerSocketChannel,对Channel和TCP参数进行配置
     * 将ServerSocketChannel设为异步非阻塞模式。它的backlog设为1024
     * 系统资源初始化成功后，将ServerSocketChannel注册到Selector，监听SelectionKey.OP_ACCEPT操作位。
     * 如果资源初始化失败，则退出
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    /**
     * 循环遍历selector
     * 休眠时间1s，无论是否有读写等事件发生，selector每隔1s被唤醒一次
     * selector也提供了一个无参的select方法，当有处于就绪状态的Channel时，selector将返回该Channel的SelectionKey集合
     * 通过对就绪状态的Channel集合进行迭代，可以进行网络的异步读写操作。
     */
    @Override
    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey = null;
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(selectionKey);
                    } catch (IOException e) {
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会自动去注册并关闭，所以不需要重复释放资源
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleInput(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isValid()) {
            //处理新接入的请求消息
            /**
             * 根据selectionKey的操作位进行判断即可获知网络事件的类型，通过ServerSocketChannel的accept接收客户端的连接请求
             * 并创建SocketChannel实例，完成上述操作后，相当于完成了TCP三次握手，TCP物理链路正式建立。我们需要将新创建的SocketChannel设置为
             * 异步非阻塞，同时也可以对其TCP参数进行设置，例如TCP接受和发送缓冲区的大小
             */
            if (selectionKey.isAcceptable()) {
                //Accept the new connection
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                //Add the new Connection to the selector
                socketChannel.register(this.selector, SelectionKey.OP_READ);
            }
            /**
             * 读取客户端的请求消息
             * 首先创建一个ByteBuffer，由于我们事先无法得知客户端发送的码流大小，开辟1个1MB的缓冲区，然后调用
             * SocketChannel的read方法读取请求码流。由于之前已经设置SocketChannel设置为异步非阻塞模式，因此它的read
             * 是非阻塞的。使用返回值进行判断，看读取到的字节数，返回值有以下三种可能的结果
             * 返回值大于0，读到了字节，对字节进行编解码
             * 返回值等于0，没有读取到字节，属于正常场景
             * 返回值为-1，链路已经关闭，需要关闭SocketChannel，释放资源
             * 当读取到码流以后，进行解码。首先对readBuffer进行flip操作，它的作用是将缓冲区当前的limit设置为position，position设置为0
             * 用于后续对缓冲区的读取操作，然后根据缓冲区可读的字节个数创建字节数组，调用ByteBuffer的get操作将缓冲区可读的字节数组复制到新创建的字节数组中
             * 最后调用字符串的构造函数创建请求消息体并打印，如果请求指令为“QUERY TIME ORDER”，则把服务器的当前时间编码后返回给客户端。
             */
            if (selectionKey.isReadable()) {
                //Read the data
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                if (readBytes > 0) {
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order : " + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString()
                            : "BAD QUERY";
                    doWrite(socketChannel, currentTime);
                } else if (readBytes < 0) {
                    //对端链路关闭
                    selectionKey.cancel();
                    socketChannel.close();
                } else {
                    //读到0字节忽略
                }
            }
        }
    }

    /**
     * 将应答消息异步发送给客户端
     * 将字符串编码成字节数组，根据字节数组的容量创建ByteBuffer，调用ByteBuffer的put操作将字节数组复制到缓冲区，然后对缓冲区进行flip操作，最后调用
     * SocketChannel的write方法将缓冲区的字节数组发送出去，由于SocketChannel是异步非阻塞的，它并不保证一次能够把需要发送的字节数组发送完，此时会出现“写半包”
     * 的问题。我们需要注册写操作，不断轮询Selector将没有发送完的ByteBuffer发送完毕，然后可以通过ByteBuffer的hasRemain()方法判断消息是否发送完成。
     *
     * @param channel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
