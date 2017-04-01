package lew.bing.observable;

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

    public <R> Observable<R> map(Function<T,R> function) {
        return new Observable<R>(c -> {
           for (Supplier<T> next:observe.getItems()) {
               c.nextSupplier(() -> {
                   return function.apply(next.get());
               });
           }
           if (observe.isComplete()) {
               c.complete();
           }
        });
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
