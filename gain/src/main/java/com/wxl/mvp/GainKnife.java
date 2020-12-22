package com.wxl.mvp;

import android.app.Activity;
import android.app.Dialog;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.mvp.knife.Knife;
import com.wxl.mvp.knife.KnifeContainer;
import com.wxl.mvp.knife.LifecycleObservable;

/**
 * create file time : 2020/12/4
 * create user : wxl
 * subscribe :
 */
public class GainKnife {


    /**
     * 不需要支持的ID，当ID被设置成该值时不进行支持
     */
    public static Class UNSUPPORTID = GainLifecycle.class;


    /**
     * 绑定一个Activity对象，如果当前Activity具有生命周期即实现了LifecycleProvider接口（如：RxAppCompatActivity）则会同时附带生命周期。
     * 当有其他对象注解 Field 的 id 指向该对象时，则具有与该对象同样的生命周期能力
     *
     * @param target
     */
    public static void bind(Activity target) {
        if (target == null) return;
        KnifeContainer.getInstance().setMainClass(target.getClass());
        Knife.findGainForKeyWordTarget(target);
    }

    /**
     * 绑定一个Fragment对象，如果当前Fragment具有生命周期即实现了LifecycleProvider接口（如：RxFragment）则会同时附带生命周期。
     * 当有其他对象注解 Field 的 id 指向该对象时，则具有与该对象同样的生命周期能力
     *
     * @param target
     */
    public static void bind(Fragment target) {
        if (target == null) return;
        KnifeContainer.getInstance().setMainClass(target.getClass());
        Knife.findGainForKeyWordTarget(target);
    }

    /**
     * 绑定一个Dialog对象，如果当前Dialog具有生命周期即实现了LifecycleProvider接口（如：RxDialog）则会同时附带生命周期。
     * 当有其他对象注解 Field 的 id 指向该对象时，则具有与该对象同样的生命周期能力
     *
     * @param target
     */
    public static void bind(Dialog target) {
        if (target == null) return;
        KnifeContainer.getInstance().setMainClass(target.getClass());
        Knife.findGainForKeyWordTarget(target);
    }

    /**
     * 绑定一个PopupWindow对象，如果当前PopupWindow具有生命周期即实现了LifecycleProvider接口（如：RxPopupWindow）则会同时附带生命周期。
     * 当有其他对象注解 Field 的 id 指向该对象时，则具有与该对象同样的生命周期能力
     *
     * @param target
     */
    public static void bind(PopupWindow target) {
        if (target == null) return;
        KnifeContainer.getInstance().setMainClass(target.getClass());
        Knife.findGainForKeyWordTarget(target);
    }


    /**
     * 绑定一个对象
     * 当该对象具有生命周期能力时会同时附带生命周期
     * 当有其他对象注解 Field 的 id 指向该对象时，则具有与该对象同样的生命周期能力
     *
     * @param
     * @param target
     */
    public static void bind(Object target) {
        if (target == null) return;
        KnifeContainer.getInstance().setMainClass(target.getClass());
        Knife.findGainForKeyWordTarget(target);
    }

    /**
     * 注册一个在使用时需要且未通过bind方法绑定的类，但又不可被无参构造初始化的对象。
     * 如：未通过bind方法绑定的 Activity,Fragment,Dialog,PopupWindow。
     * 或者带参构造类
     *
     * @param target\
     * @link bind
     */
    @Deprecated
    public static void registerUnableConstructorTarget(Object target) {
        if(target == null) return;
        registerUnableConstructorTarget(target, UNSUPPORTID);
    }

    /**
     * 注册一个在使用时需要且未通过bind方法绑定的类，但又不可被无参构造初始化的对象。
     * 如：未通过bind方法绑定的 Activity,Fragment,Dialog,PopupWindow。
     * 或者带参构造类
     *
     * @param target 被注册对象
     * @param life   需要与哪个已经被初始化并注册，且具有生命周期能力的类同步被释放。
     * @link bind
     */
    @Deprecated
    public static void registerUnableConstructorTarget(Object target, Class life) {
        if(target == null || life == null) return;
        Knife.findUnSupportConstructorGainForKeyWordTarget(target, life);
    }


    /**
     * 唤醒一个被unBind释放的类
     * 如:同一个类被指定了两次，被另一个销毁之后，需调用该方法恢复到当前应用的类，否则将会有空指针风险
     *
     * @param target
     */
    public static void onResumeWhenReleased(Class target) {
        if(target == null) return;
        Knife.onResumeTargetLifecycle(target);
    }


    /**
     * 取消注册
     * 取消注册一个通过registerUnableConstructorTarget 注册的对象
     *
     * @param targetCls 被注册对象
     * @link unBind
     */
    @Deprecated
    public static void unRegisterUnableConstructorTarget(Class targetCls) {
        if(targetCls == null) return;
        KnifeContainer.getInstance().removeUnSupport(targetCls.getName());
        LifecycleObservable.get().unAttachLifecycle(targetCls);
    }

    /**
     * 取消注册，取消一个通过registerUnableConstructorTarget 注解的类。
     *
     * @param targetCls 被注册的类型
     */
    public static void unBind(Class targetCls) {
        if(targetCls == null) return;
        KnifeContainer.getInstance().unRelated(targetCls.getName());
        LifecycleObservable.get().unAttachLifecycle(targetCls);
    }
}
