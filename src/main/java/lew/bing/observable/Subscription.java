package lew.bing.observable;

import java.util.*;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
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
    private CompletableFuture<Void> continueFuture;

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
        continueFuture = new CompletableFuture<>();
        continueFuture.complete(null);
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
        //全部都用CompletableFuture
        if (Observe.STATUS.DONE.equals(arg)) {
            if (handleDone != null) {
                continueFuture.whenComplete((v,t) -> {
                    System.out.println("check");
                    handleDone.run();
                    o.deleteObserver(subscription);
                });



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
                    //使用CompletableFuture
                    System.out.println(continueFuture);
                    continueFuture = continueFuture.thenApplyAsync((v) -> {
                        try {
                            T call = _arg.call();
                            return call;
                        }catch (Exception e) {
                            if (handleException != null) {
                                handleException.accept(e);
                            }
                            return null;
                        }
                    },Threads.service()).thenAcceptAsync(handleNext);
                    //注意这样的话主线程会退掉
                }else {
                    try {
                        Future<T> submit = Threads.submit(_arg);
                        handleNext.accept(submit.get());
                    }catch (Exception e) {
                        if (handleException != null) {
                            handleException.accept(e);
                        }
                    }
                }
            }catch (ClassCastException e) {
                //转换失败就不管
            }
        }
    }



    public void unsubscript(){
        this.observe.deleteObserver(this.subscription);
    }
}
