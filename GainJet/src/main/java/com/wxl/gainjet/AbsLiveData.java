package com.wxl.gainjet;

import androidx.lifecycle.LiveData;

/**
 * create file time : 2021/1/29
 * create user : wxl
 * subscribe :
 */
public abstract class AbsLiveData<T> extends LiveData<T> {


    public void refresh(T t) {
        setValue(t);
    }

    public void syncRefresh(T t) {
        postValue(t);
    }

    @Override
    protected void onActive() {
        super.onActive();
        //绑定
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        //销毁
    }
}
