package com.wxl.mvp.lifecycle;

/**
 * create file time : 2021/1/4
 * create user : wxl
 * subscribe :
 */
public interface OnGainAttachFinishCallback {

    /**
     * 异步绑定完成
     * @param target 绑定完成的对象
     */
    void onSyncAttachFinish(Object target);
}
