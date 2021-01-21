package com.wxl.aptmvp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainField;
import com.wxl.aptmvp.api.AP;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.base.BaseActivity;
import com.wxl.mvp.http.DialogCallback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.lifecycle.OnGainAttachFinishCallback;
import com.wxl.mvp.util.Loog;

public class MainActivity extends BaseActivity {

    @GainField(target = P.class, life = MainActivity.class)
    AP pxx;

    @GainApi
    Api api;

    TextView textView;

    @Override
    protected void onCreateBindViewBefore(@Nullable Bundle savedInstanceState) {
        Loog.methodE("start bind");
        GainKnife.bindSync(this, new OnGainAttachFinishCallback() {
            @Override
            public void onSyncAttachFinish(Object target) {
                Loog.methodE(target.getClass().getName());
            }
        });
        Loog.methodE("end bind");
        //GainKnife.registerUnableConstructorTarget(new P(0));
    }

    @Override
    protected int onCreateBindViewLayoutId(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }


    @Override
    protected void onCreateBindViewChanged(@Nullable Bundle savedInstanceState) {
        textView = findViewById(R.id.textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GainHttp.load(api.loadConfig("Home.getConfig"), new DialogCallback<String>() {

                    @Override
                    public Dialog getLoadingDialog(Context context) {
                        Loog.methodE(context.getClass().getName());
                        return new LoadingDialog(context);
                    }

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
    protected void onResume() {
        //如：TwoActivity 与 MainActivity 同时指定了P这个对象，在TwoActivity unBind释放后，
        //P中的m字段及M类中包含的@Gain类注解字段都将会被设置为空，
        //如需要恢复这些值，则需要调用该方法
        //GainKnife.onResumeWhenReleased(getClass());
        super.onResume();
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
        Loog.methodE("start unbind");
        GainKnife.unBind(getClass());
        Loog.methodE("end unbind");
    }
}