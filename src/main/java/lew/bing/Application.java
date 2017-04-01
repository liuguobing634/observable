package lew.bing;

import lew.bing.observable.Observable;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Application {

    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> from = Observable.from(1, 2, 3, 4, 5);
        from.subscript(n -> {
            System.out.println(n);
        });
        Thread.sleep(2000);
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
    }

}
