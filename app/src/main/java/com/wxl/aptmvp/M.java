package com.wxl.aptmvp;

import com.wxl.apt_annotation.ApiEvent;
import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.mvp.http.Callback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
@GainLifecycle(life = MainActivity.class,event = ApiEvent.STOP)
public class M implements GainActivityLifecycle {

    @GainApi
    Api api;

    @GainLifecycle(life = MainActivity.class,event = ApiEvent.DESTROY)
    public void loadApk(){
        if(api != null) {
            GainHttp.load(api.loadConfig("Home.getConfig"), new Callback<String>() {
                @Override
                public void onSuccess(String s) {
                    Loog.methodE(s);
                }

                @Override
                public void onFailed(String error) {
                    Loog.methodE(error);
                }
            });
        }
    }

    @Override
    public void onResume() {
        Loog.methodE("onResume");
    }

    @Override
    public void onPause() {
        Loog.methodE("onPause");
    }

    @Override
    public void onStop() {
        Loog.methodE("onStop");
    }

    @Override
    public void onGainAttach() {
        Loog.methodE("onGainAttach");
    }

    @Override
    public void onGainDetach() {
        Loog.methodE("onGainDetach");
    }
}
