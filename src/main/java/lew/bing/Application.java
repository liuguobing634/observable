package lew.bing;

import lew.bing.observable.Observable;
import lew.bing.observable.Observe;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Application {

    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> from = Observable.from(1, 2, 3, 4, 5);
        from.subscript(n -> {
            System.out.println(n);
        });
//        Thread.sleep(2000);
        from.subscript(n -> {
            System.out.println(n);
        },null,() -> {
            System.out.println("完成");
        });
        from.map(c -> {
//            System.out.println(c);
            return c+2;
        }).subscript(n -> {
            System.out.println(n);
        });

        Observe<Integer> objectObserve = new Observe<>();
        Observable<Integer> integerObservable = new Observable<>(objectObserve);
        integerObservable.subscript(s -> {
            System.out.println(s);
        });
        objectObserve.next(12);
        objectObserve.next(23);
        objectObserve.nextSupplier(() -> {
            System.out.println("hello");
            return 33;
        });
        integerObservable.map(s -> s+1).subscript(s -> {
            System.out.println("haha");
            System.out.println(s);
        });
    }

}
