package lew.bing.observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Observe<T> extends java.util.Observable {

    private boolean complete;

    private final List<Supplier<T>> items = new ArrayList<>();

    public enum STATUS {
        DONE
    }

    public boolean isComplete() {
        return complete;
    }

    public List<Supplier<T>> getItems() {
        return items;
    }

    @Override
    public synchronized void addObserver(Observer o) {

        super.addObserver(o);
        //当添加了注册源才执行
        for (Supplier<T> item:items) {
            o.update(this,item.get());
        }
        if(complete) {
            o.update(this,STATUS.DONE);
        }

    }

    //有两个方法，next和complete
    public synchronized void next(T next){
        if (this.complete) {
            //已经结束
        }else {
            //添加到items中
            items.add(() -> next);
            //如果通知源不空，那么就通知

            this.setChanged();
            this.notifyObservers(next);
        }
    }

    public synchronized void nextSupplier(Supplier<T> next) {
        if (!this.complete) {
            items.add(next);
            this.setChanged();
            if (this.countObservers() > 0) {
                this.notifyObservers(next.get());
            }
        }
    }

    public synchronized void complete(){
        //通知注册器
        this.complete = true;
        this.setChanged();
        this.notifyObservers(STATUS.DONE);
    }

}
