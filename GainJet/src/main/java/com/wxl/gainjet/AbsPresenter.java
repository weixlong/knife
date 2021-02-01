package com.wxl.gainjet;


import androidx.annotation.CallSuper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatDialogFragment;
import com.trello.rxlifecycle2.components.support.RxDialogFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

/**
 * create file time : 2021/1/29
 * create user : wxl
 * subscribe :
 */
public abstract class AbsPresenter extends ViewModel implements LifecycleObserver {

    private RxAppCompatActivity mRxAppCompatActivity;
    private RxAppCompatDialogFragment mRxAppCompatDialogFragment;
    private RxFragment mRxFragment;
    private RxDialogFragment mRxDialogFragment;
    private RxFragmentActivity mRxFragmentActivity;

    protected String TAG = AbsPresenter.class.getSimpleName();

    /**
     * 创建Presenter后调用
     *
     * @param context
     */
    @CallSuper
    public void onCreated(RxAppCompatActivity context, Object... args) {
        mRxAppCompatActivity = context;
        TAG = getClass().getSimpleName();
    }


    /**
     * 创建Presenter后调用
     *
     * @param context
     */
    @CallSuper
    public void onCreated(RxAppCompatDialogFragment context, Object... args) {
        mRxAppCompatDialogFragment = context;
        TAG = getClass().getSimpleName();
    }


    /**
     * 创建Presenter后调用
     *
     * @param context
     */
    @CallSuper
    public void onCreated(RxFragment context, Object... args) {
        mRxFragment = context;
        TAG = getClass().getSimpleName();
    }


    /**
     * 创建Presenter后调用
     *
     * @param context
     */
    @CallSuper
    public void onCreated(RxDialogFragment context, Object... args) {
        mRxDialogFragment = context;
        TAG = getClass().getSimpleName();
    }


    /**
     * 创建Presenter后调用
     *
     * @param context
     */
    @CallSuper
    public void onCreated(RxFragmentActivity context, Object... args) {
        mRxFragmentActivity = context;
        TAG = getClass().getSimpleName();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Loog.d(TAG, "onCreate");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Loog.d(TAG, "onStart");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Loog.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Loog.d(TAG, "onPause");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Loog.d(TAG, "onStop");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @CallSuper
    public void onDestroy() {
        Loog.d(TAG, "onDestroy");
        if (mRxAppCompatActivity != null) {
            mRxAppCompatActivity.getLifecycle().removeObserver(this);
            mRxAppCompatActivity = null;
        }

        if (mRxAppCompatDialogFragment != null) {
            mRxAppCompatDialogFragment.getLifecycle().removeObserver(this);
            mRxAppCompatDialogFragment = null;
        }

        if (mRxFragment != null) {
            mRxFragment.getLifecycle().removeObserver(this);
            mRxFragment = null;
        }

        if (mRxDialogFragment != null) {
            mRxDialogFragment.getLifecycle().removeObserver(this);
            mRxDialogFragment = null;
        }

        if (mRxFragmentActivity != null) {
            mRxFragmentActivity.getLifecycle().removeObserver(this);
            mRxFragmentActivity = null;
        }
    }


}
