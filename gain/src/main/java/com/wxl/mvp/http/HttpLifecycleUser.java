package com.wxl.mvp.http;

/**
 * create file time : 2020/12/12
 * create user : wxl
 * subscribe : 生命周期调用者
 */
public class HttpLifecycleUser {
    private Class userClass;
    private String name;

    public HttpLifecycleUser(Class userClass, String name) {
        this.userClass = userClass;
        this.name = name;
    }

    public Class getUserClass() {
        return userClass;
    }

    public void setUserClass(Class aClass) {
        this.userClass = aClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
