package com.wxl.aptmvp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainField;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.base.BaseActivity;
import com.wxl.mvp.http.Callback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/12/15
 * create user : wxl
 * subscribe :
 */
public class TwoActivity extends BaseActivity {

    @GainField(target = P.class,life = TwoActivity.class)
    AP px;

    @GainApi
    Api api;

    TextView textView;

    @Override
    protected int onCreateBindViewLayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreateBindViewChanged(@Nullable Bundle savedInstanceState) {
        Loog.methodE("start bind");
        GainKnife.bind(this);
        Loog.methodE("end bind");
        textView = findViewById(R.id.textView);
        GainHttp.exe(api.loadConfig("Home.getConfig"), new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                textView.setText(s);
            }

            @Override
            public void onFailed(String error) {
                textView.setText(error);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Loog.methodE("start unbind");
        GainKnife.unBind(getClass());
        Loog.methodE("end unbind");
        super.onDestroy();
    }
}
