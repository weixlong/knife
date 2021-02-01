package com.wxl.gainjet;

import androidx.lifecycle.ViewModel;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * create file time : 2021/1/29
 * create user : wxl
 * subscribe :
 */
public abstract class AbsViewModel<D extends AbsLiveData, T> extends ViewModel {


    interface OnViewModelClearedCallback<T> {
        void onCleared(AbsLiveData<T> data);
    }

    protected D d;

    private OnViewModelClearedCallback<T> mOnViewModelClearedCallback;

    protected void setOnClearedCallback(OnViewModelClearedCallback<T> callback) {
        this.mOnViewModelClearedCallback = callback;
    }


    public void refresh(T t) {
        AbsLiveData<T> data = getLiveData();
        if (data != null) {
            data.refresh(t);
        }
    }

    public void syncRefresh(T t) {
        AbsLiveData<T> data = getLiveData();
        if (data != null) {
            data.syncRefresh(t);
        }
    }

    public AbsLiveData<T> getLiveData() {
        return d;
    }

    protected D newInstanceData() {
        Type type = getClass().getGenericSuperclass();
        //ParameterizedType参数化类型，即泛型
        ParameterizedType p = (ParameterizedType) type;
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Class<D> d = (Class<D>) p.getActualTypeArguments()[0];
        try {
            return d.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AbsViewModel() {
        d = newInstanceData();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mOnViewModelClearedCallback != null) {
            mOnViewModelClearedCallback.onCleared(d);
        }
    }
}
