package com.wxl.mvp.http;

import android.app.Dialog;
import android.content.Context;

/**
 * create file time : 2021/1/21
 * create user : wxl
 * subscribe :
 */
public interface DialogCallback<T> extends Callback<T> {


    /**
     * 自定义加载对话框
     * @param context 当前栈顶Activity对应的context
     * @return 返回空则不显示对话框
     */
    Dialog getLoadingDialog(Context context);
}
