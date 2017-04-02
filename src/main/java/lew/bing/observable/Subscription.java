package lew.bing.observable;

import java.util.*;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
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

    private boolean async;

    private  class _subscription implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            Subscription.this.handle(o,arg);
        }
    }

    private _subscription subscription;

    public Subscription(Observe<T> observe, Consumer<T> handleNext, Consumer<Exception> handleException, Runnable handleDone){
        this(observe,handleNext,handleException,handleDone,false);
    }

    public Subscription(Observe<T> observe, Consumer<T> handleNext, Consumer<Exception> handleException, Runnable handleDone,boolean async){
        this.observe = observe;
        this.handleNext = handleNext;
        this.handleException = handleException;
        this.handleDone = handleDone;
        this.async = async;
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
        }else if (arg instanceof Callable){
            try {
                Callable<T> _arg = (Callable<T>) arg;
                //如果是异步就异步执行
                if (async){
                    Threads.submit(() -> {
                        System.out.println("test");
                        try {
                            T call = _arg.call();
                            handleNext.accept(call);
                        }catch (Exception e) {
                            if (handleException != null) {
                                handleException.accept(e);
                            }
                        }

                        return null;
                    });
                    //注意这样的话主线程会退掉
                }else {
                    Future<T> submit = Threads.submit(_arg);
                    handleNext.accept(submit.get());
                }
            }catch (ClassCastException e) {
                //转换失败就不管
            }catch (Exception e) {
                if (handleException != null) {
                    handleException.accept(e);
                }
            }
        }
    }



    public void unsubscript(){
        this.observe.deleteObserver(this.subscription);
    }
}
