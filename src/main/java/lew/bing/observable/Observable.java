package lew.bing.observable;

import java.util.Observer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Observable<T> {

    private Observe<T> observe;

    public Observable(Consumer<Observe<T>> consumer){
        observe = new Observe<>();
        consumer.accept(observe);
    }

    public Observable(Observe<T> observe) {
        this.observe = observe;
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
    public <R> Observable<R> map(Function<T,R> function) {
        //修正一下，为observe添加observer，并处理它
        Observe<R> rObserve = new Observe<>();
        Observable<R> rObservable = new Observable<>(rObserve);
        Observer observer = (o,arg) -> {
            if (Observe.STATUS.DONE.equals(arg)){
                rObserve.complete();
            }else if (arg instanceof Exception) {
                //handler exception给下一个
                rObserve.exception((Exception) arg);
            }else if (arg instanceof Supplier) {
                Supplier<T> _arg = (Supplier<T>) arg;
                rObserve.nextSupplier(() -> function.apply(_arg.get()));
            }
        };
        this.observe.addObserver(observer);
        return rObservable;
    }

    public Subscription<T> subscript(Consumer<T> handleNext,Consumer<Exception> handleException,Runnable handleDone){
        return new Subscription<>(observe,handleNext,handleException,handleDone);
    }

    public Subscription<T> subscript(Consumer<T> handleNext) {
        return subscript(handleNext,null,null);
    }

    public Subscription<T> subscript(Consumer<T> handleNext,Consumer<Exception> handleException) {
        return subscript(handleNext,handleException,null);
    }



}
