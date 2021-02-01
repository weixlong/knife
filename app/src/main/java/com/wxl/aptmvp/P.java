package com.wxl.aptmvp;

import com.wxl.apt_annotation.GainField;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.aptmvp.api.AP;
import com.wxl.gainjet.Loog;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */

public class P extends AP implements GainActivityLifecycle {

    @GainField(target = A.class,life = MainActivity.class)
    A a;

    @GainField(target = B.class,life = MainActivity.class)
    B b;

    @Override
    public void onResume() {
        Loog.methodE("onResume");
        if(m != null) {
            m.loadApk();
        }
        //loadConfig();
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
    public void onGainAttach(Object[] args) {
        GainKnife.getPreArgs().setArgs(A.class,"this is pre args");
        Loog.methodE("onGainAttach args : "+args[0]);
    }

    @Override
    public void onGainDetach() {
        Loog.methodE("onGainDetach");
    }

    @Override
    public void start() {
        Loog.e("start");
    }
}
