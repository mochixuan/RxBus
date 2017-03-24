package com.wx.rxbusdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.wx.library.RxStickyBus;

import rx.functions.Action1;

public class SecondActivity extends RxAppCompatActivity {

    private TextView tvDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvDetail = (TextView) findViewById(R.id.tv_receiver);
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subcriberSticky();
            }
        });
    }

    public void subcriberSticky() {
        RxStickyBus.getInstance()
                .toObservableSticky(Bean.class)
                .compose(this.<Bean>bindUntilEvent(ActivityEvent.STOP))       //解决内存溢出
                .subscribe(new Action1<Bean>() {
                    @Override
                    public void call(Bean bean) {
                        String courses="" ;
                        for (String s:bean.getCourses()) {
                            courses = courses+"\n"+s;
                        }
                        tvDetail.setText("姓名:"+bean.getName()+"\n"+"课程："+courses);
                        RxStickyBus.getInstance().removeStickyEvent(String.class);  //这样实现单次传递数据
                    }
                });
    }


}
