package cn.liulin.authentic_netty.netty.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * cn.liulin.authentic_netty.netty.msgpack$
 *
 * @author ll
 * @date 2021-05-24 09:53:20
 **/
public class MsgPackApi {
    public static void main(String[] args) throws IOException {
        List<Integer> list = new ArrayList<>(3);
        list.add(1);
        list.add(2);
        list.add(3);
        MessagePack messagePack = new MessagePack();
        byte[] write = messagePack.write(list);
        List<Integer> read = messagePack.read(write, Templates.tList(Templates.TInteger));
        System.out.println(read);
    }
}
