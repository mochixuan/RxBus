package com.wx.rxbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wx.library.RxBus;
import com.wx.library.RxLifeBus;
import com.wx.library.RxStickyBus;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity implements View.OnClickListener{

    private TextView tvReceiver;
    private Bean mBeans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        tvReceiver = (TextView) findViewById(R.id.tv_receiver);

        mBeans = new Bean();
        List<String> lists = new ArrayList<>();
        lists.add("数学");
        lists.add("语文");
        mBeans.setCourses(lists);
        mBeans.setName("莫辞");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                subcriberString();
                break;
            case R.id.btn2:
                subcriberRxlife();
                break;
            case R.id.btn3:
                subcriberSticky();
                break;
            case R.id.btn4:
                RxBus.getInstance().post("RxBus 数据");
                break;
            case R.id.btn5:
                RxLifeBus.getInstance().post("RxLifeBus 数据");
                break;
            case R.id.btn6:
                RxStickyBus.getInstance().postSticky("RxStickyBus 数据");
                break;
            case R.id.btn7:
                RxStickyBus.getInstance().postSticky(mBeans);
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void setTvReceivers(String data){
        tvReceiver.setText(data);
    }

    public void subcriberString() {
        RxBus.getInstance()
                .addSubscription(this, RxBus.getInstance().toObservable(String.class)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                setTvReceivers(s);
                            }
                        }));
    }

    public void subcriberRxlife() {
        RxLifeBus.getInstance()
                .toObservable(String.class,this.<String>bindUntilEvent(ActivityEvent.STOP))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        setTvReceivers(s);
                    }
                });

    }

    public void subcriberSticky() {
        RxStickyBus.getInstance()
                .toObservableSticky(String.class)
                .compose(this.<String>bindUntilEvent(ActivityEvent.STOP))        //解决内存溢出
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        setTvReceivers(s);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxBus.getInstance().unSubscribe(this);
    }
}
