package cn.liulin.authentic_netty.netty.msgpack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * cn.liulin.authentic_netty.netty.msgpack$
 *
 * @author ll
 * @date 2021-05-24 14:27:01
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Message
public class UserInfo implements Serializable {
    private int age;
    private String name;

    @Override
    public String toString() {
        return "UserInfo{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
