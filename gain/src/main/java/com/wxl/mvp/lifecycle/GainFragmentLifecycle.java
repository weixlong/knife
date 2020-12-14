package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/11
 * create user : wxl
 * subscribe :
 */
public interface GainFragmentLifecycle extends GainActivityLifecycle {
    /**
     * 仅对fragment 类型有效
     */
    void onCreateView();

    /**
     * 仅对fragment 类型有效
     */
    void onDestroyView();
}
