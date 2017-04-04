package lew.bing.observable;

import lew.bing.functions.MyConsumer;
import lew.bing.functions.MyFunction;
import lew.bing.http.MyHttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Observer;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * 订阅与发布
 * 采取消息机制
 * 发布者发布消息给订阅者
 * 订阅者处理消息
 *
 */
public abstract class Observable<T> {

    private static Logger logger = Logger.getGlobal();


    //转换消息
    public <S> Observable<S> map(MyFunction<T,S> function){
        //生成function栈
        return new MapObservable<>(this,function);
    }

    public abstract Subscription subscript(MyConsumer<T> next, Consumer<Exception> exceptionConsumer, Runnable complete);
    public  Subscription subscript(MyConsumer<T> next){
        return     this.subscript(next,null,null);
    }
    public  Subscription subscript(MyConsumer<T> next, Runnable complete) {
        return this.subscript(next,null,complete);
    }


    public static <T> Observable<T> of (T next) {
        return new DefaultObservable<T>((o) -> {
            o.next(next);
            o.complete();
        });
    }

    public static <T> Observable<T> from(T... items) {
        return new DefaultObservable<T>((o) -> {
           for (T next:items) {
               o.next(next);
           }
           o.complete();
        });
    }

    public static Observable<Void> timeout(long time) {
        return of(null).map((a) -> {
            Thread.sleep(time);
            return null;
        });
    }

    public static Observable<Void> interval(final long time) {
        //这个不采取消息发布策略
        return new Observable<Void>() {
            @Override
            public Subscription subscript(MyConsumer<Void> next, Consumer<Exception> exceptionConsumer, Runnable complete) {
                Subscription subscription = new Subscription() {

                    private CompletableFuture<Void> future;

                    @Override
                    public void handleMessage(Message<?> message) {
                        switch (message.getType()) {
                            case DONE:
                                if (future != null) {
                                    future.whenComplete((a,e) -> {
                                       if (e != null && exceptionConsumer != null) {
                                           exceptionConsumer.accept(new Exception(e));
                                       }
                                       if (complete != null) {
                                           complete.run();
                                       }
                                        this.unsubscript();
                                    });
                                }else {
                                    if (complete != null) {
                                        complete.run();
                                    }
                                }
                                break;
                            case EXCEPTION:
                                if (exceptionConsumer != null) {
                                    exceptionConsumer.accept((Exception) message.getValue());
                                }
                                break;
                            case NORMAL:
                                if (next != null){
                                    CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                                        try {
                                            next.accept(null);
                                        } catch (Exception e) {
                                            if (exceptionConsumer != null) {
                                                exceptionConsumer.accept(e);
                                            }
                                        }
                                    });
                                    if (future == null) {
                                        future = future1;
                                    }else {
                                        future = future.thenCombine(future1,(a,v) ->null);
                                    }
                                }
                                break;
                        }
                    }

                    @Override
                    public void unsubscript() {

                    }
                };
                //每隔一段时间给subscription发送消息
                Threads.run(() -> {
                    while (true){
                        try {
                            Thread.sleep(time);
                            subscription.handleMessage(Message.Next(null));
                        } catch (InterruptedException e) {
                            subscription.handleMessage(Message.Throw(e));
                            subscription.handleMessage(Message.Done);
                            break;
                        }
                    }


                });
                return subscription;
            }
        };
    }

    public static Observable<MyHttpResponse> httpRequest(HttpUriRequest request) {
        return Observable.of(request).map(r -> {
            CloseableHttpClient build = HttpClientBuilder.create().build();
            CloseableHttpResponse execute = build.execute(request);
            return new MyHttpResponse(execute);

        });
    }


    private static class MapObservable<S,T> extends Observable<S> {

        private Observable<T> origin;
        private MyFunction<T,S> function;

        public MapObservable(Observable<T> origin, MyFunction<T, S> function) {
            this.origin = origin;
            this.function = function;
        }

        @Override
        public Subscription subscript(MyConsumer<S> next, Consumer<Exception> exceptionConsumer, Runnable complete) {


            return origin.subscript(t->{
                //这里只传递
                logger.info(t+"");
                next.accept(function.apply(t));
            },exceptionConsumer,complete);
        }
    }

    private static class DefaultObservable<T> extends Observable<T> {

        private Observe<T> observe;
        private MessageManage manage;

        DefaultObservable(){
            manage = new MessageManage();
            observe = new Observe<T>(manage);
        }

        DefaultObservable(Consumer<Observe<T>> init){
            this();
            init.accept(observe);

        }

        @Override
        public Subscription subscript(final MyConsumer<T> next, final Consumer<Exception> exceptionConsumer, final Runnable complete) {
            Subscription subscription = new Subscription() {
                protected CompletableFuture<Void> future;


                @Override
                public void handleMessage(Message<?> message) {
                    switch (message.getType()) {
                        case DONE:
                            //等所有的信息处理完再处理

                            final Runnable r = complete!=null?complete:() ->{
                              logger.info("完成");
                            };
                            if(future != null){
                                future.whenCompleteAsync(((aVoid, throwable) -> {
                                    r.run();
                                }));
                            }else {
                                r.run();
                            }
                            break;
                        case NORMAL:
                            //这里才使用线程
                            if (next != null) {
                                T value = (T) message.getValue();
                                CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                                    try {
                                        next.accept(value);
                                    } catch (Exception e) {
                                        if (exceptionConsumer != null){
                                            exceptionConsumer.accept(e);
                                        }
                                    }
                                }, Threads.service());
                                if (future == null){
                                    future = future1;
                                }else {
                                 future = future.thenCombine(future1,(u,v) ->{return null;});
                                }
                            }
                            break;
                        case EXCEPTION:
                            if (exceptionConsumer != null) {
                                Exception e = (Exception) message.getValue();
                                exceptionConsumer.accept(e);
                            }
                            break;
                    }
                }

                @Override
                public void unsubscript() {
                    manage.remove(this);
                }
            };
            manage.onSubscript(subscription);
            return subscription;
        }
    }


}
