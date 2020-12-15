package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/10
 * create user : wxl
 * subscribe :
 */
public interface GainActivityLifecycle extends Lifecycle {

    /**
     * 在该方法之前解绑则将不会回调
     */
    void onResume();

    /**
     * 在该方法之前解绑则将不会回调
     */
    void onPause();

    /**
     * 在该方法之前解绑则将不会回调
     */
    void onStop();

}
