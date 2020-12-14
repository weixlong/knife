package com.wxl.apt_annotation;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe : MVP生命周期
 */
public enum Event {

    /**
     * MVP绑定
     */
    ATTACH,

    /**
     * 对应Fragment 生命周期的onActivityCreate,
     * 只有绑定的是Fragment类型时才会有效
     * 最终是否会调用该生命周期，取决于使用者绑定生命周期的时机
     */
    ACTIVITYCREATE,

    /**
     * 生命周期RESUM，跟随注解life一起调用
     * 最终是否会调用该生命周期，取决于使用者绑定生命周期的时机
     */
    RESUME,

    /**
     * 生命周期PAUSE，跟随注解life一起调用
     * 最终是否会调用该生命周期，取决于使用者绑定生命周期的时机
     */
    PAUSE,

    /**
     * 生命周期STOP，跟随注解life一起调用
     * 最终是否会调用该生命周期，取决于使用者绑定生命周期的时机
     */
    STOP,


    /**
     * 对应Fragment 生命周期的onDestroyView,
     * 只有绑定的是Fragment类型时才会有效
     * 最终是否会调用该生命周期，取决于使用者绑定生命周期的时机
     */
    DESTROYVIEW,

    /**
     * MVP解绑
     */
    DETACH
}
