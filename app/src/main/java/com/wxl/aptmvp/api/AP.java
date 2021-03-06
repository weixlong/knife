package com.wxl.aptmvp.api;

import android.app.Dialog;
import android.content.Context;

import com.wxl.apt_annotation.ApiEvent;
import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainField;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.aptmvp.Api;
import com.wxl.aptmvp.LoadingDialog;
import com.wxl.aptmvp.M;
import com.wxl.aptmvp.MainActivity;
import com.wxl.gainjet.Loog;
import com.wxl.mvp.http.DialogCallback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;

/**
 * create file time : 2020/12/12
 * create user : wxl
 * subscribe :
 */
public abstract class AP implements GainActivityLifecycle {

    @GainField(target = M.class, life = MainActivity.class)
    public M m;

    @GainApi
    Api api;

    public abstract void start();

    @GainLifecycle(life = MainActivity.class, event = ApiEvent.STOP)
    protected void loadConfig() {
        if (api != null) {
            GainHttp.load(api.loadConfig("Home.getConfig"), new DialogCallback<String>() {

                @Override
                public Dialog getLoadingDialog(Context context) {
                    return new LoadingDialog(context);
                }

                @Override
                public void onSuccess(String s) {
                    Loog.methodE("loadApk success");
                }

                @Override
                public void onFailed(String error) {
                    Loog.methodE(error);
                }
            });
        }
    }
}
