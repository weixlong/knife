package com.wxl.aptmvp;

import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.mvp.util.Loog;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public class M {

    @GainApi()
    Api api;

    @GainLifecycle(life = MainActivity.class)
    public void loadApk(){
        Loog.methodE("M create success");
    }
}
