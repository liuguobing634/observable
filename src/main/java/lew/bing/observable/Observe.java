package lew.bing.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Observe<T> extends java.util.Observable {

    private boolean complete;

    private final List<Callable<T>> items = new ArrayList<>();


    public enum STATUS {
        DONE
    }

    public boolean isComplete() {
        return complete;
    }

    public List<Callable<T>> getItems() {
        return items;
    }

    @Override
    public synchronized void addObserver(Observer o) {

        super.addObserver(o);
        //当添加了注册源才执行
        for (Callable<T> item:items) {
            o.update(this,item);
        }
        if(complete) {
            o.update(this,STATUS.DONE);
        }

    }

    //有两个方法，next和complete.处理不放到这里
    public synchronized void next(T next){
        if (this.complete) {
            //已经结束
        }else {
            //添加到items中
            Callable<T> s = () -> next;
            this.nextSupplier(s);
        }
    }

    public synchronized void nextSupplier(Callable<T> next) {
        if (!this.complete) {
            items.add(next);
            this.setChanged();
            this.notifyObservers(next);
        }
    }

    public synchronized void complete(){
        //通知注册器
        this.complete = true;
        this.setChanged();
        this.notifyObservers(STATUS.DONE);
    }

    public synchronized void exception(Exception e) {
        this.setChanged();
        this.notifyObservers(e);
    }

}
