package lew.bing.observable;

import java.util.*;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public interface Subscription{

    //改成接口
    void handleMessage(Message<?> message);

    void unsubscript();

}
