package com.wx.library;

import java.util.HashMap;

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

public class RxBus {

    private static RxBus INSTANCE;
    private Subject<Object,Object> mSubject;
    private HashMap<String, CompositeSubscription> mSubscriptionMap;

    private RxBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
        mSubscriptionMap = new HashMap<>();
    }

    public static RxBus getInstance() {
        if (INSTANCE==null) {
            synchronized (RxBus.class) {
                if (INSTANCE==null) {
                    INSTANCE = new RxBus();
                }
            }
        }
        return INSTANCE;
    }

    public void post(Object object){
        mSubject.onNext(object);
    }

    public <T> Observable<T> toObservable(final Class<T> type){
        return mSubject.ofType(type);
    }

    public void addSubscription(Object o, Subscription subscription) {
        String key = o.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(subscription);
        } else {
            CompositeSubscription compositeSubscription = new CompositeSubscription();
            compositeSubscription.add(subscription);
            mSubscriptionMap.put(key, compositeSubscription);
        }
    }

    public void unSubscribe(Object o) {
        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)){
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).unsubscribe();
        }
        mSubscriptionMap.remove(key);
    }

}
