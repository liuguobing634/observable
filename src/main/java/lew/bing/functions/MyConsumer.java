package lew.bing.functions;

/**
 * Created by 刘国兵 on 2017/4/4.
 */
@FunctionalInterface
public interface MyConsumer<T> {

    void accept(T v) throws Exception;

}
