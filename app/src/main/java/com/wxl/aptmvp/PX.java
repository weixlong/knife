package com.wxl.aptmvp;

import com.wxl.apt_annotation.GainField;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/12/15
 * create user : wxl
 * subscribe :
 */
public class PX extends AP {

    @GainField(target = M.class, life = TwoActivity.class)
    M m;

    @GainLifecycle(life = MainActivity.class)
    @Override
    public void onResume() {
        Loog.methodE("onResume");
        //m.loadApk();
    }


    @GainLifecycle(life = MainActivity.class)
    @Override
    public void onPause() {
        Loog.methodE("onPause");
    }

    @Override
    public void onStop() {
        Loog.methodE("onStop");
    }


    @Override
    public void onGainAttach() {
        Loog.methodE("onGainAttach");
    }

    @Override
    public void onGainDetach() {
        Loog.methodE("onGainDetach");
    }

    @Override
    public void start() {
        Loog.methodE("start");
    }
}
