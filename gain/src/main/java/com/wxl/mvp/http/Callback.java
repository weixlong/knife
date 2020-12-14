package com.wxl.mvp.http;

/**
 * Created by wxl on 2019/6/28.
 */

public interface Callback<T>  {


    void onSuccess(T t);

    void onFailed(String error);
}
