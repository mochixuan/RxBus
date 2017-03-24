package com.wx.library;

import com.trello.rxlifecycle.LifecycleTransformer;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxLifeBus {

    private static RxLifeBus INSTANCE;
    private Subject<Object,Object> mSubject;

    private RxLifeBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxLifeBus getInstance() {
        if (INSTANCE==null) {
            synchronized (RxLifeBus.class) {
                if (INSTANCE==null) {
                    INSTANCE = new RxLifeBus();
                }
            }
        }
        return INSTANCE;
    }

    public void post(Object object){
        mSubject.onNext(object);
    }

    public <T> Observable<T> toObservable(final Class<T> type,LifecycleTransformer transformer){
        return mSubject.ofType(type).compose(transformer);
    }


}
