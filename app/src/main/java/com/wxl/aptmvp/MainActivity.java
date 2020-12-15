package com.wxl.aptmvp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainField;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.base.BaseActivity;
import com.wxl.mvp.http.Callback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.util.Loog;

public class MainActivity extends BaseActivity {

    @GainField(target = P.class, life = MainActivity.class)
    AP pxx;

    @GainApi
    Api api;

    TextView textView;

    @Override
    protected void onCreateBindViewBefore(@Nullable Bundle savedInstanceState) {
        //GainKnife.registerUnableConstructorTarget(new P(0));
    }

    @Override
    protected int onCreateBindViewLayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreateBindViewChanged(@Nullable Bundle savedInstanceState) {
        Loog.e("start bind");
        GainKnife.bind(this);
        Loog.e("end bind");
        textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GainHttp.exe(api.loadConfig("Home.getConfig"), new Callback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        textView.setText(s);
                        startActivity(new Intent(MainActivity.this, TwoActivity.class));
                    }

                    @Override
                    public void onFailed(String error) {
                        textView.setText(error);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //GainKnife.unRegisterUnableConstructorTarget(getClass());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Loog.e("start unbind");
        GainKnife.unBind(getClass());
        Loog.e("end unbind");
    }
}