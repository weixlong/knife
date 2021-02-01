package com.wxl.gainjet;

import androidx.lifecycle.ViewModelProvider;

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
public class PresenterFactory {


    /**
     * 创建一个Presenter对象
     *
     * @param context
     * @param pClass
     * @param <P>
     * @return
     */
    public static <P extends AbsPresenter> P create(RxAppCompatActivity context, Class<P> pClass, Object... args) {
        P p = new ViewModelProvider(context, new ViewModelProvider.NewInstanceFactory()).get(pClass);
        p.onCreated(context, args);
        context.getLifecycle().addObserver(p);
        return p;
    }


    /**
     * 创建一个Presenter对象
     *
     * @param context
     * @param pClass
     * @param <P>
     * @return
     */
    public static <P extends AbsPresenter> P create(RxAppCompatDialogFragment context, Class<P> pClass, Object... args) {
        P p = new ViewModelProvider(context, new ViewModelProvider.NewInstanceFactory()).get(pClass);
        p.onCreated(context, args);
        context.getLifecycle().addObserver(p);
        return p;
    }

    /**
     * 创建一个Presenter对象
     *
     * @param context
     * @param pClass
     * @param <P>
     * @return
     */
    public static <P extends AbsPresenter> P create(RxFragment context, Class<P> pClass, Object... args) {
        P p = new ViewModelProvider(context, new ViewModelProvider.NewInstanceFactory()).get(pClass);
        p.onCreated(context, args);
        context.getLifecycle().addObserver(p);
        return p;
    }

    /**
     * 创建一个Presenter对象
     *
     * @param context
     * @param pClass
     * @param <P>
     * @return
     */
    public static <P extends AbsPresenter> P create(RxDialogFragment context, Class<P> pClass, Object... args) {
        P p = new ViewModelProvider(context, new ViewModelProvider.NewInstanceFactory()).get(pClass);
        p.onCreated(context, args);
        context.getLifecycle().addObserver(p);
        return p;
    }

    /**
     * 创建一个Presenter对象
     *
     * @param context
     * @param pClass
     * @param <P>
     * @return
     */
    public static <P extends AbsPresenter> P create(RxFragmentActivity context, Class<P> pClass, Object... args) {
        P p = new ViewModelProvider(context, new ViewModelProvider.NewInstanceFactory()).get(pClass);
        p.onCreated(context, args);
        context.getLifecycle().addObserver(p);
        return p;
    }


}
