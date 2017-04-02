package lew.bing.observable;

import lew.bing.http.MyHttpException;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by 刘国兵 on 2017/4/2.
 */
public class ObservableTest {
    @Test
    public void request() throws Exception {
        Observable.request(new HttpGet("https://www.zhihu.com/xxx"))
                .subscript(resp -> {
                    System.out.println(resp.getStatusCode());
                    System.out.println(resp.getContent());
                }, e -> {
                    if (e instanceof MyHttpException) {
                        System.out.println(((MyHttpException) e).getCode());
                        System.out.println(((MyHttpException) e).getStatus());
                    }
                    e.printStackTrace();
                },null);
        System.out.println("哈哈哈");
        Thread.sleep(10000);
    }

    @Test
    public void of() throws Exception {
        Observable.of(2)
                .subscript(s -> {
                    System.out.println(Thread.currentThread());
                    System.out.println(s);
                });
    }
}