package lew.bing.observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 刘国兵 on 2017/4/1.
 */
public class Threads {

    private static ExecutorService service = Executors.newCachedThreadPool();


    public static void run(Runnable runnable){
        service.execute(runnable);
    }

    public static void shutdown(){
        service.shutdown();
        service.shutdownNow();
    }

}
