package lew.bing.observable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by 刘国兵 on 2017/3/31.
 */
public class Subscription<T> implements Observer{

    private  Observe<T> observe;
    private Consumer<T> handleNext;
    private Consumer<Exception> handleException;
    private Runnable handleDone;

    public Subscription(Observe<T> observe, Consumer<T> handleNext, Consumer<Exception> handleException, Runnable handleDone){
        this.observe = observe;
        this.handleNext = handleNext;
        this.handleException = handleException;
        this.handleDone = handleDone;
        observe.addObserver(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(java.util.Observable o, Object arg) {
        if (Observe.STATUS.DONE.equals(arg)) {
            if (handleDone != null) {
                handleDone.run();
            }
        }else if (arg instanceof Exception) {
            if (handleException != null) {
                handleException.accept((Exception) arg);
            }
        }else if (arg != null){
            try {
                T _arg = (T) arg;
                handleNext.accept(_arg);
            }catch (Exception e) {
                //转换失败就不管
            }
        }
    }

    public void unsubscript(){
        this.observe.deleteObserver(this);
    }
}
