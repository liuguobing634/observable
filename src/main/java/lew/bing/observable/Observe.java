package lew.bing.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 * 之后将不再继承Observable
 * 这将只负责发布消息，订阅者不通过它注册
 */
public class Observe<T>  {


    private MessageManage<T> manage;

    public Observe(MessageManage<T> manage) {
        this.manage = manage;
    }

    //有两个方法，next和complete.处理不放到这里
    public synchronized void next(T next){
        manage.onMessageReceive(next);
    }


    public synchronized void complete(){
        //通知注册器
        manage.onDone();
    }

    public synchronized void exception(Exception e) {
        manage.onException(e);
    }

}
