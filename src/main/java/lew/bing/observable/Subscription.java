package lew.bing.observable;

import java.util.*;
import java.util.Observable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Subscription<T>{

    private  Observe<T> observe;
    private Consumer<T> handleNext;
    private Consumer<Exception> handleException;
    private Runnable handleDone;
    //创建一个内部类实现observer接口

    private  class _subscription implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            Subscription.this.handle(o,arg);
        }
    }

    private _subscription subscription;

    public Subscription(Observe<T> observe, Consumer<T> handleNext, Consumer<Exception> handleException, Runnable handleDone){
        this.observe = observe;
        this.handleNext = handleNext;
        this.handleException = handleException;
        this.handleDone = handleDone;
        subscription = new _subscription();
        observe.addObserver(subscription);
    }

    @SuppressWarnings("unchecked")
    private void handle(Observable o, Object arg){
        if (Observe.STATUS.DONE.equals(arg)) {
            if (handleDone != null) {
                handleDone.run();
                o.deleteObserver(subscription);
            }
        }else if (arg instanceof Exception) {
            if (handleException != null) {
                handleException.accept((Exception) arg);
            }
        }else if (arg instanceof Supplier){
            try {
                Supplier<T> _arg = (Supplier<T>) arg;
                handleNext.accept(_arg.get());
            }catch (Exception e) {
                //转换失败就不管
            }
        }
    }



    public void unsubscript(){
        this.observe.deleteObserver(this.subscription);
    }
}
