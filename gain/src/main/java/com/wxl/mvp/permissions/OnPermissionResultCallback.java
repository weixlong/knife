package com.wxl.mvp.permissions;

public interface OnPermissionResultCallback {
    void onRequestPermissionSuccess(int requestCode);
    void onRequestPermissionFailed(int requestCode);
}
