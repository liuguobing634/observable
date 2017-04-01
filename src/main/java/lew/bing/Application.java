package lew.bing;

import lew.bing.observable.Observable;
import lew.bing.observable.Observe;
import lew.bing.observable.Threads;

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
        objectObserve.next(55);

        Observable.interval(1000).subscript(h -> {
            System.out.println(Thread.currentThread().getName() + ": 喜欢你");
        },null,() -> {
            System.out.println("完成了");
        });
        Observable.timeout(1000).subscript(h -> {
            System.out.println(Thread.currentThread().getName() + ": 哈哈");
        },null,() -> System.out.println("over"));
        try {
            Thread.sleep(10000);
            Threads.shutdown();
        }catch (Exception e) {
            Threads.shutdown();
        }
    }

}
