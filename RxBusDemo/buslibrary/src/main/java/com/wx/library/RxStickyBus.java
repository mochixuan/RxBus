package com.wx.library;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

//注意释放内存
public class RxStickyBus {

    private static volatile RxStickyBus INSTANCE;
    private final Subject<Object,Object> mSubject;
    private Map<String, Object> mSubscriptionMap;

    private RxStickyBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
        mSubscriptionMap = new ConcurrentHashMap<>();
    }

    public static RxStickyBus getInstance(){
        if(INSTANCE==null){
            synchronized (RxStickyBus.class){
                if(INSTANCE==null){
                    INSTANCE=new RxStickyBus();
                }
            }
        }
        return INSTANCE;
    }

    public void post(Object object){
        mSubject.onNext(object);
    }

    public <T> Observable<T> toObservable(Class<T> type){
        return mSubject.ofType(type);
    }

    public void postSticky(Object object) {
        synchronized (mSubscriptionMap) {
            mSubscriptionMap.put(object.getClass().getName(), object);
        }
        post(object);
    }

    public <T> Observable<T> toObservableSticky(final Class<T> type) {
        Observable<T> observable = toObservable(type);
        synchronized (mSubscriptionMap) {
            final Object object = mSubscriptionMap.get(type.getName());
            if (object != null) {
                return observable.mergeWith(Observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        subscriber.onNext(type.cast(object));               //如果接收后移除可在后面添加 removeStickyEvent(type);
                    }
                }));
            } else {
                return observable;
            }
        }
    }

    public <T> T removeStickyEvent(Class<T> type) {
        synchronized (mSubscriptionMap) {
            return type.cast(mSubscriptionMap.remove(type.getName()));
        }
    }

    public void removeAllStickyEvents() {
        synchronized (mSubscriptionMap) {
            mSubscriptionMap.clear();
        }
    }

}
