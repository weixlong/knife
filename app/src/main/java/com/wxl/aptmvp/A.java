package com.wxl.aptmvp;

import com.wxl.mvp.lifecycle.GainActivityLifecycle;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2021/1/4
 * create user : wxl
 * subscribe :
 */
public class A implements GainActivityLifecycle {

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
    public void onGainAttach(Object[] args) {
        Loog.methodE("onGainAttach");
    }

    @Override
    public void onGainDetach() {
        Loog.methodE("onGainDetach");
    }
}
