package lew.bing.observable;

import java.util.concurrent.Callable;

/**
 * Created by 刘国兵 on 2017/4/3.
 * 订阅消息
 */
public class Message<T> {

    /**
    *  消息信息枚举
     *  完成，异常和普通消息
    * */
    public enum MessageType {
        DONE,EXCEPTION,NORMAL
    }

    private MessageType type;
    private T value;

    private Message(MessageType type, T value) {
        this.type = type;
        this.value = value;
    }

    public MessageType getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public static final Message<Void> Done = new Message<>(MessageType.DONE,null);


    public static <T> Message<T> Next(T value){
        return new Message<T>(MessageType.NORMAL,value);
    }

    public static Message<Exception> Throw(Exception e) {
        return new Message<>(MessageType.EXCEPTION,e);
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
