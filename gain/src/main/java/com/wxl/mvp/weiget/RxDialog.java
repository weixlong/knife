package com.wxl.mvp.weiget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;
import com.wxl.mvp.event.DialogEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe :
 */
public class RxDialog extends Dialog implements LifecycleProvider<DialogEvent> {

    private final BehaviorSubject<DialogEvent> lifecycleSubject = BehaviorSubject.create();

    public RxDialog(@NonNull Context context) {
        super(context);
    }

    public RxDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(DialogEvent.CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(DialogEvent.START);
    }

    @Override
    public void show() {
        super.show();
        lifecycleSubject.onNext(DialogEvent.SHOW);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleSubject.onNext(DialogEvent.STOP);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        lifecycleSubject.onNext(DialogEvent.DISMISS);
    }

    @Override
    public Observable<DialogEvent> lifecycle() {
        return lifecycleSubject.hide();
    }


    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull DialogEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindView(getCurrentFocus());
    }
}
