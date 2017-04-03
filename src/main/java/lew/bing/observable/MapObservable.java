package lew.bing.observable;

import java.util.Observer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by 刘国兵 on 2017/4/3.
 *     //针对map的
 */
public class MapObservable<T,R> extends Observable<R> implements Observer{

    private Function<R,T> function;

    public MapObservable(Observable<T> observable,Function<R,T> function) {
        super(new Observe<R>());
        Observe<T> observe = observable.observe;
        observe.addObserver(this);
        this.function = function;
    }





    @Override
    public void update(java.util.Observable o, Object arg) {

    }
}
