package lew.bing.observable;

import lew.bing.http.MyHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Observer;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Observable<T> {

    protected Observe<T> observe;
    private boolean async;

    public Observable(Consumer<Observe<T>> consumer){
        observe = new Observe<>();
        consumer.accept(observe);
    }

    public Observable(Observe<T> observe) {
        this.observe = observe;
    }

    public Observable(Consumer<Observe<T>> consumer,boolean async){
        observe = new Observe<>();
        consumer.accept(observe);
        this.async = async;
    }

    public Observable(Observe<T> observe,boolean async) {
        this.observe = observe;
        this.async = async;
    }

    public static <T>  Observable<T> of(final T next) {
        return new Observable<T>(c -> {
           c.next(next);
           c.complete();
        });
    }

    public static <T> Observable<T> from(final T... items) {
        return new Observable<T>(c -> {
           for (T item:items) {
               c.next(item);
           }
           c.complete();
        });
    }

    @SuppressWarnings("unchecked")
    public static Observable interval(long time){
        //waiting time to send next
        Observe observe = new Observe();
        //使用线程池
        Threads.run(() -> {
            while (true){
                try {
                    Thread.sleep(time);
                    observe.next(1);
                } catch (InterruptedException e) {
                    //线程完成
                    observe.complete();
                    break;
                }
            }

        });
        return new Observable(observe);
    }
    @SuppressWarnings("unchecked")
    public static Observable timeout(long time) {
        Observe observe = new Observe();
        Threads.run(() -> {
            try {
                Thread.sleep(time);
                observe.next(1);
                observe.complete();
            } catch (InterruptedException e) {
                observe.complete();
            }
        });
        return new Observable(observe);

    }

    public static Observable<MyHttpResponse> request(HttpUriRequest request) {
        HttpClient client = HttpClientBuilder.create().build();
        Observe<MyHttpResponse> entity = new Observe<>();
        entity.nextSupplier(() -> {
            HttpResponse execute = client.execute(request);
            MyHttpResponse myHttpResponse = new MyHttpResponse(execute,entity);
            entity.complete();
            return myHttpResponse;
        });
        return new Observable<>(entity,true);
    }

    @SuppressWarnings("unchecked")
    public <R> Observable<R> map(Function<T,R> function) {
        //修正一下，为observe添加observer，并处理它
        Observe<R> rObserve = new Observe<>();
        Observable<R> rObservable = new Observable<>(rObserve,this.async);
        Observer observer = (o,arg) -> {
            if (Observe.STATUS.DONE.equals(arg)){
                rObserve.complete();
            }else if (arg instanceof Exception) {
                //handler exception给下一个
                rObserve.exception((Exception) arg);
            }else if (arg instanceof Callable) {
                //对callable进行合并
                //如果异步用CompleteFuture,否则用直接用Executor
                Callable<T> callable = (Callable<T>) arg;
                rObserve.nextSupplier(() -> {
                    Future<T> submit = Threads.submit(callable);
                    T t = submit.get();
                    return function.apply(t);
                });

            }
        };
        this.observe.addObserver(observer);
        return rObservable;
    }

    public Subscription<T> subscript(Consumer<T> handleNext,Consumer<Exception> handleException,Runnable handleDone){
        return new Subscription<>(observe,handleNext,handleException,handleDone,async);
    }

    public Subscription<T> subscript(Consumer<T> handleNext) {
        return subscript(handleNext,null,null);
    }

    public Subscription<T> subscript(Consumer<T> handleNext,Consumer<Exception> handleException) {
        return subscript(handleNext,handleException,null);
    }



}
