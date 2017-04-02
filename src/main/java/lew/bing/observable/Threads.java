package lew.bing.observable;

import java.util.concurrent.*;

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

    public static <T> Future<T> submit(Callable<T> callable){
        return service.submit(callable);
    }

}
