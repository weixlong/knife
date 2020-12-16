package com.wxl.mvp.lifecycle;

/**
 * create file time : 2020/12/11
 * create user : wxl
 * subscribe :
 */
public interface GainFragmentLifecycle extends GainActivityLifecycle {

    /**
     * 仅对fragment 类型有效
     * 在该方法之前解绑则将不会回调
     */
    void onDestroyView();
}
