package lew.bing.functions;

/**
 * Created by 刘国兵 on 2017/4/4.
 */
@FunctionalInterface
public interface MyFunction<T,U> {

    U apply(T v) throws Exception;

}
