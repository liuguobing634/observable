package lew.bing;

import lew.bing.observable.Observable;
import lew.bing.observable.Observe;
import lew.bing.observable.Threads;
import org.apache.http.client.methods.HttpGet;

import java.time.LocalDateTime;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Application {

    public static void main(String[] args) throws InterruptedException {
        Observable.of(2).map(n -> {
            Thread.sleep(5000);
            System.out.println(Thread.currentThread().getName() +": " + n);
            return n+3;
        }).subscript(s -> {
            System.out.println(s);
        },null,() ->{
            Threads.shutdown();
        });
        Observable.httpRequest(new HttpGet("http://www.baidu.com"))
                .subscript(n -> {
                    System.out.println(n.getContent());
                },null,null);
        Observable.interval(300).subscript(v -> {
            System.out.println(LocalDateTime.now());
        });
    }

}
