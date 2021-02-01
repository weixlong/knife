package com.wxl.aptmvp;

import com.wxl.gainjet.Loog;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;

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
        Loog.methodE("onGainAttach : "+args[0]);
    }

    @Override
    public void onGainDetach() {
        Loog.methodE("onGainDetach");
    }
}
