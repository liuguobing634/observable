# observable
对观察者模式的探索

更正了subscription,取消实现observer接口，而是内部类来实现。
修改了map方法，之前的会不监听原observable的之后的事件


添加interval和timeout执行定时任务