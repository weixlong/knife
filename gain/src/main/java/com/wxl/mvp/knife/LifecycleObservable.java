package com.wxl.mvp.knife;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.wxl.mvp.event.DialogEvent;
import com.wxl.mvp.event.PopupWindowEvent;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;
import com.wxl.mvp.lifecycle.GainDialogLifecycle;
import com.wxl.mvp.lifecycle.GainFragmentLifecycle;
import com.wxl.mvp.lifecycle.GainPopLifecycle;
import com.wxl.mvp.lifecycle.Lifecycle;
import com.wxl.mvp.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * create file time : 2020/12/3
 * create user : wxl
 * subscribe :
 */
public class LifecycleObservable {

    private static class LO {
        public static LifecycleObservable mLifecycleObservable = new LifecycleObservable();
    }

    private LifecycleObservable() {
    }

    public static LifecycleObservable get() {
        return LO.mLifecycleObservable;
    }

    /**
     * 生命周期回调
     */
    private HashMap<Class, List<Lifecycle>> lifecycles = new HashMap<>();

    /**
     * 生命周期添加标记
     */
    private List<String> lifecycleTags = new ArrayList<>();

    /**
     * 生命周期Disposable
     */
    private static HashMap<Class, Disposable> lifecycleDisposables = new HashMap<>();


    /**
     * 回调对应的生命周期
     *
     * @param
     * @param lifecycle
     */
    public synchronized void observableAttachEvent(Class key, final Lifecycle lifecycle) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        lifecycle.onGainAttach();
        addLifecycle(key, lifecycle);
    }

    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observablePopEvent(Class key, Observable<PopupWindowEvent> observable, final GainPopLifecycle lifecycle) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        lifecycle.onGainAttach();
        addLifecycle(key, lifecycle);
        if(lifecycleDisposables.containsKey(key))return;
        lifecycleDisposables.put(key, observable.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PopupWindowEvent>() {
                    @Override
                    public void accept(PopupWindowEvent activityEvent) throws Exception {
                        List<Lifecycle> baseLifecycles = lifecycles.get(key);
                        for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                            if (baseLifecycles.get(i) != null) {
                                GainPopLifecycle popLifecycle = (GainPopLifecycle) baseLifecycles.get(i);
                                switch (activityEvent) {
                                    case SHOW:
                                        popLifecycle.show();
                                        break;
                                    case DISMISS:
                                        popLifecycle.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableDialogEvent(Class key, Observable<DialogEvent> observable, final GainDialogLifecycle lifecycle) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        lifecycle.onGainAttach();
        addLifecycle(key, lifecycle);
        if(lifecycleDisposables.containsKey(key))return;
        lifecycleDisposables.put(key, observable.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DialogEvent>() {
                    @Override
                    public void accept(DialogEvent activityEvent) throws Exception {
                        List<Lifecycle> baseLifecycles = lifecycles.get(key);
                        for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                            if (baseLifecycles.get(i) != null) {
                                GainDialogLifecycle dialogLifecycle = (GainDialogLifecycle) baseLifecycles.get(i);
                                switch (activityEvent) {
                                    case START:
                                        dialogLifecycle.onStart();
                                        break;
                                    case SHOW:
                                        dialogLifecycle.show();
                                        break;
                                    case STOP:
                                        dialogLifecycle.onStop();
                                        break;
                                    case DISMISS:
                                        dialogLifecycle.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableActivityEvent(Class key, Observable<ActivityEvent> observable, final GainActivityLifecycle lifecycle, boolean isLoadAttach) {
        if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
                return;
            }
        }
        if (isLoadAttach) {
            lifecycle.onGainAttach();
        }
        addLifecycle(key, lifecycle);
        if(lifecycleDisposables.containsKey(key))return;
        lifecycleDisposables.put(key, observable.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ActivityEvent>() {
                    @Override
                    public void accept(ActivityEvent activityEvent) throws Exception {
                        List<Lifecycle> baseLifecycles = lifecycles.get(key);
                        for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                            if (baseLifecycles.get(i) != null) {
                                GainActivityLifecycle activityLifecycle = (GainActivityLifecycle) baseLifecycles.get(i);
                                switch (activityEvent) {
                                    case RESUME:
                                        activityLifecycle.onResume();
                                        break;
                                    case PAUSE:
                                        activityLifecycle.onPause();
                                        break;
                                    case STOP:
                                        activityLifecycle.onStop();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableFragmentEvent(Class key, Observable<FragmentEvent> observable, final GainFragmentLifecycle lifecycle, boolean isLoadAttach) {
        if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
                return;
            }
        }
        if (isLoadAttach) {
            lifecycle.onGainAttach();
        }
        addLifecycle(key, lifecycle);
        if(lifecycleDisposables.containsKey(key))return;
        lifecycleDisposables.put(key, observable.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FragmentEvent>() {
                    @Override
                    public void accept(FragmentEvent fragmentEvent) throws Exception {
                        List<Lifecycle> baseLifecycles = lifecycles.get(key);
                        for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                            if (baseLifecycles.get(i) != null) {
                                GainFragmentLifecycle fragmentLifecycle = (GainFragmentLifecycle) baseLifecycles.get(i);
                                switch (fragmentEvent) {
                                    case RESUME:
                                        fragmentLifecycle.onResume();
                                        break;
                                    case PAUSE:
                                        fragmentLifecycle.onPause();
                                        break;
                                    case STOP:
                                        fragmentLifecycle.onStop();
                                        break;
                                    case DESTROY_VIEW:
                                        fragmentLifecycle.onDestroyView();
                                        break;
                                }
                            }
                        }

                    }
                }));
    }


    private synchronized void addLifecycle(Class key, Lifecycle lifecycle) {
        lifecycleTags.add(lifecycle.getClass().getName());
        if (lifecycles.containsKey(key)) {
            List<Lifecycle> baseLifecycles = lifecycles.get(key);
            if (CollectionUtils.isNull(baseLifecycles)) {
                baseLifecycles = new ArrayList<>();
            }
            baseLifecycles.add(lifecycle);
        } else {
            List<Lifecycle> baseLifecycles = new ArrayList<>();
            baseLifecycles.add(lifecycle);
            lifecycles.put(key, baseLifecycles);
        }
    }

    public void unAttachLifecycle(Class key) {
        if (lifecycles.containsKey(key)) {
            List<Lifecycle> baseLifecycles = lifecycles.remove(key);
            if (CollectionUtils.isNotEmpty(baseLifecycles)) {
                for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                    Lifecycle lifecycle = baseLifecycles.get(i);
                    if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                        lifecycleTags.remove(lifecycle.getClass().getName());
                    }
                    lifecycle.onGainDetach();
                }
                baseLifecycles.clear();
            }
        }
        if (lifecycleDisposables.containsKey(key)) {
            Disposable disposable = lifecycleDisposables.remove(key);
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }


}
