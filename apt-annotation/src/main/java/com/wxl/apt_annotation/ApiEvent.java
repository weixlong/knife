package com.wxl.apt_annotation;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe : 对应Http请求的生命周期，应用于在该生命周期时结束该持有者所拥有的请求对象
 */
public enum ApiEvent {

    /**
     * 生命周期
     */
    CREATE,

    /**
     * Fragment 对应的生命周期
     */
    ACTIVITYCREATE,

    /**
     * 生命周期
     */
    START,

    /**
     * 生命周期
     */
    RESUME,

    /**
     * 生命周期
     */
    PAUSE,


    /**
     * 生命周期
     */
    STOP,

    /**
     * Fragment 生命周期
     */
    DESTROYVIEW,

    /**
     * 生命周期
     */
    DESTROY
}
