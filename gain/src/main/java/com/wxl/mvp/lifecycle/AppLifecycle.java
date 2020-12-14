package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/14
 * create user : wxl
 * subscribe :
 */
public interface AppLifecycle {

    /**
     * 处于前台
     */
    void onFront();


    /**
     * 处于后台
     */
    void onBackground();
}
