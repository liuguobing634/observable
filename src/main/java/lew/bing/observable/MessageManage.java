package lew.bing.observable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 刘国兵 on 2017/4/3.
 */
public class MessageManage<T> {

    private List<Subscription> subscriptions = new ArrayList<>();
    private List<Message<T>> messages = new ArrayList<>();
    private boolean complete;

    public synchronized void onSubscript(Subscription subscription){
        messages.forEach(m -> subscription.handleMessage(m));
        if (complete) {
            subscription.handleMessage(Message.Done);
        }
    }

    public synchronized void onMessageReceive(T entity){
        if (!complete) {
            Message<T> next = Message.Next(entity);
            this.messages.add(next);
            subscriptions.forEach(s -> {
                s.handleMessage(next);
            });
        }

    }

    public void onDone(){
        this.complete = true;
        //传给subscription
        Iterator<Subscription> iterator = subscriptions.iterator();
        while (iterator.hasNext()) {
            iterator.next().handleMessage(Message.Done);
            iterator.remove();
        }
    }

    public void onException(Exception e) {
        //传给subscription一个异常消息
        subscriptions.forEach(s -> {
            s.handleMessage(Message.Throw(e));
        });
    }

    public void remove(Subscription subscription) {
        subscriptions.remove(subscription);
    }



}
