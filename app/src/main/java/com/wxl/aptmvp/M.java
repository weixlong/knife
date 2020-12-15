package com.wxl.aptmvp;

import com.wxl.apt_annotation.ApiEvent;
import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.mvp.http.Callback;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public class M {

    @GainApi
    Api api;

    @GainLifecycle(life = MainActivity.class,event = ApiEvent.STOP)
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
        } else {
            api = GainHttp.api(Api.class);
        }
    }
}
