package com.bluetooth.bluetoothdemo.bean;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Mersens on 2016/12/21 09:37
 * Email:626168564@qq.com
 */

public class RxBus {

    //PublishSubject可以先订阅事件，然后在某一时刻通过调用去触发响应
    private static final PublishSubject<Object> bus = PublishSubject.create();
    private static RxBus rxBus;
    private RxBus(){

    }
    //获取实例
    public static RxBus getInstance() {
        if (rxBus == null) {
            synchronized (RxBus.class){
                if(rxBus == null){
                    rxBus = new RxBus();
                }
            }
        }
        return rxBus;
    }
    //发送数据
    public void send(Object obj) {
        bus.onNext(obj);
    }

    //监听订阅事件
    public Observable<Object> toObservable() {
        return bus;
    }
}
