package com.wxl.mvp.weiget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.wxl.mvp.event.PopupWindowEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe :
 */
public class RxPopupWindow extends PopupWindow implements LifecycleProvider<PopupWindowEvent> {

    private final BehaviorSubject<PopupWindowEvent> lifecycleSubject = BehaviorSubject.create();

    public RxPopupWindow(Context context) {
        super(context);
    }

    public RxPopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RxPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RxPopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RxPopupWindow() {
    }

    public RxPopupWindow(View contentView) {
        super(contentView);
    }

    public RxPopupWindow(int width, int height) {
        super(width, height);
    }

    public RxPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public RxPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        lifecycleSubject.onNext(PopupWindowEvent.SHOW);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        lifecycleSubject.onNext(PopupWindowEvent.SHOW);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        lifecycleSubject.onNext(PopupWindowEvent.SHOW);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        lifecycleSubject.onNext(PopupWindowEvent.SHOW);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        lifecycleSubject.onNext(PopupWindowEvent.DISMISS);
    }

    @NonNull
    @Override
    public Observable<PopupWindowEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull PopupWindowEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindView(getContentView());
    }
}
