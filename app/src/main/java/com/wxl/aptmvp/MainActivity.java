package com.wxl.aptmvp;


import android.os.Bundle;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wxl.apt_annotation.GainField;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.util.Loog;

public class MainActivity extends RxAppCompatActivity {

    @GainField(target = P.class,life = MainActivity.class)
    AP pxx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Loog.e("start bind");
        //GainKnife.registerUnableConstructorTarget(new P(0));
        GainKnife.bind(this);
        Loog.e("end bind");
        pxx.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //GainKnife.unRegisterUnableConstructorTarget(getClass());
        GainKnife.unBind(getClass());
    }
}