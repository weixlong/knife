package com.wxl.aptmvp;

import android.app.Application;

import com.wxl.mvp.GainNote;
import com.wxl.mvp.lifecycle.AppLifecycle;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/12/14
 * create user : wxl
 * subscribe :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GainNote.init(this, new AppLifecycle() {
            @Override
            public void onFront() {
                Loog.e("app onFront");
            }

            @Override
            public void onBackground() {
                Loog.e("app onBackground");
            }
        },true);
    }
}
