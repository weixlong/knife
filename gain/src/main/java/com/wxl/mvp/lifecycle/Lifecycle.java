package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/11
 * create user : wxl
 * subscribe :
 */
public interface Lifecycle {
    void onGainAttach(Object[] args);
    void onGainDetach();
}
