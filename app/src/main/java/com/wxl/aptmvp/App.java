package com.wxl.aptmvp;

import android.app.Application;

import com.wxl.mvp.GainNote;
import com.wxl.mvp.http.GainHttp;

/**
 * create file time : 2020/12/14
 * create user : wxl
 * subscribe :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GainNote.init(this, GainHttp.option().api(Api.class)
                .baseUrl("https://api.xuanjige.net")
                .build());
    }
}
