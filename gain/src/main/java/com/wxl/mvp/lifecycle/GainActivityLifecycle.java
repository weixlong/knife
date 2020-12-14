package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/10
 * create user : wxl
 * subscribe :
 */
public interface GainActivityLifecycle extends Lifecycle {

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
