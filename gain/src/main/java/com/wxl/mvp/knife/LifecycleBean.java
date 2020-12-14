package com.wxl.mvp.knife;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.wxl.apt_annotation.ApiEvent;

/**
 * create file time : 2020/12/14
 * create user : wxl
 * subscribe :
 */
public class LifecycleBean {
    LifecycleProvider lifecycleProvider;
    private ApiEvent event;

    public LifecycleBean(LifecycleProvider lifecycleProvider, ApiEvent event) {
        this.lifecycleProvider = lifecycleProvider;
        this.event = event;
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycleProvider;
    }

    public void setLifecycleProvider(LifecycleProvider lifecycleProvider) {
        this.lifecycleProvider = lifecycleProvider;
    }

    public ApiEvent getEvent() {
        return event;
    }

    public void setEvent(ApiEvent event) {
        this.event = event;
    }
}
