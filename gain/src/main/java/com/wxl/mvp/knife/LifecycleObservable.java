package com.wxl.mvp.knife;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

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
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * create file time : 2020/12/3
 * create user : wxl
 * subscribe :
 */
public class LifecycleObservable {

    private static class LO {
        public static LifecycleObservable mLifecycleObservable = new LifecycleObservable();
    }

    private final int ATTACH_WHAT = 0;
    private final int UNTATCH_WHAT = -1;
    private final int POPUP_SHOW_WHAT = 1;
    private final int POPUP_DISMISS_WHAT = 2;
    private final int DIALOG_START_WHAT = 3;
    private final int DIALOG_SHOW_WHAT = 4;
    private final int DIALOG_STOP_WHAT = 5;
    private final int DIALOG_DISMISS_WHAT = 6;
    private final int ACTIVITY_RESUME_WHAT = 8;
    private final int ACTIVITY_PAUSE_WHAT = 9;
    private final int ACTIVITY_STOP_WHAT = 10;
    private final int FRAGMENT_DESTORY_VIEW_WHAT = 12;

    private LifecycleObservable() {
        syncSwitchHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ATTACH_WHAT:
                        if (msg.obj instanceof SyncMsg) {
                            SyncMsg msgg = (SyncMsg) msg.obj;
                            Lifecycle lifecycle = msgg.lifecycle;
                            lifecycle.onGainAttach();
                            addLifecycle(msgg.key, lifecycle);
                            exeLifecycleSync(msgg);
                        }
                        break;
                    case UNTATCH_WHAT:
                        if (msg.obj instanceof Lifecycle) {
                            Lifecycle lifecycle = (Lifecycle) msg.obj;
                            lifecycle.onGainDetach();
                        }
                        break;
                    case POPUP_SHOW_WHAT:
                        if (msg.obj instanceof GainPopLifecycle) {
                            GainPopLifecycle lifecycle = (GainPopLifecycle) msg.obj;
                            lifecycle.show();
                        }
                        break;
                    case POPUP_DISMISS_WHAT:
                        if (msg.obj instanceof GainPopLifecycle) {
                            GainPopLifecycle lifecycle = (GainPopLifecycle) msg.obj;
                            lifecycle.dismiss();
                        }
                        break;
                    case DIALOG_START_WHAT:
                        if (msg.obj instanceof GainDialogLifecycle) {
                            GainDialogLifecycle lifecycle = (GainDialogLifecycle) msg.obj;
                            lifecycle.onStart();
                        }
                        break;
                    case DIALOG_SHOW_WHAT:
                        if (msg.obj instanceof GainDialogLifecycle) {
                            GainDialogLifecycle lifecycle = (GainDialogLifecycle) msg.obj;
                            lifecycle.show();
                        }
                        break;
                    case DIALOG_STOP_WHAT:
                        if (msg.obj instanceof GainDialogLifecycle) {
                            GainDialogLifecycle lifecycle = (GainDialogLifecycle) msg.obj;
                            lifecycle.onStop();
                        }
                        break;
                    case DIALOG_DISMISS_WHAT:
                        if (msg.obj instanceof GainDialogLifecycle) {
                            GainDialogLifecycle lifecycle = (GainDialogLifecycle) msg.obj;
                            lifecycle.dismiss();
                        }
                        break;
                    case ACTIVITY_RESUME_WHAT:
                        if (msg.obj instanceof GainActivityLifecycle) {
                            GainActivityLifecycle lifecycle = (GainActivityLifecycle) msg.obj;
                            lifecycle.onResume();
                        }
                        break;
                    case ACTIVITY_PAUSE_WHAT:
                        if (msg.obj instanceof GainActivityLifecycle) {
                            GainActivityLifecycle lifecycle = (GainActivityLifecycle) msg.obj;
                            lifecycle.onPause();
                        }
                        break;
                    case ACTIVITY_STOP_WHAT:
                        if (msg.obj instanceof GainActivityLifecycle) {
                            GainActivityLifecycle lifecycle = (GainActivityLifecycle) msg.obj;
                            lifecycle.onStop();
                        }
                        break;
                    case FRAGMENT_DESTORY_VIEW_WHAT:
                        if (msg.obj instanceof GainFragmentLifecycle) {
                            GainFragmentLifecycle lifecycle = (GainFragmentLifecycle) msg.obj;
                            lifecycle.onDestroyView();
                        }
                        break;
                }
            }
        };
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
     * 获得已经绑定且执行过的popup生命周期
     */
    private static HashMap<Class, List<PopupWindowEvent>> lifecycleSyncPopup = new HashMap<>();

    /**
     * 获得已经绑定且执行过的dialog生命周期
     */
    private static HashMap<Class, List<DialogEvent>> lifecycleSyncDialog = new HashMap<>();

    /**
     * 获得已经绑定且执行过的Activity生命周期
     */
    private static HashMap<Class, List<ActivityEvent>> lifecycleSyncActivity = new HashMap<>();

    /**
     * 获得已经绑定且执行过的Fragment生命周期
     */
    private static HashMap<Class, List<FragmentEvent>> lifecycleSyncFragment = new HashMap<>();


    private Handler syncSwitchHandler;


    /**
     * 执行
     *
     * @param msgg
     */
    private void exeLifecycleSync(SyncMsg msgg) {
        if (msgg.lifecycle instanceof GainDialogLifecycle) {
            exeDialogLifecycleEventSync(msgg.key, msgg.lifecycle);
        } else if (msgg.lifecycle instanceof GainPopLifecycle) {
            exePopupLifecycleEventSync(msgg.key, msgg.lifecycle);
        } else if (msgg.lifecycle instanceof GainFragmentLifecycle) {
            exeFragmentLifecycleEventSync(msgg.key, msgg.lifecycle);
        } else if (msgg.lifecycle instanceof GainActivityLifecycle) {
            exeActivityLifecycleEventSync(msgg.key, msgg.lifecycle);
        }
    }


    /**
     * 回调对应的生命周期
     *
     * @param
     * @param lifecycle
     */
    public synchronized void observableAttachEvent(Class key, final Lifecycle lifecycle, boolean isSync) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        if (isSync) {
            sendAttachMsg(key, lifecycle);
        } else {
            lifecycle.onGainAttach();
        }

    }


    private void sendAttachMsg(Class key, Lifecycle lifecycle) {
        Message message = syncSwitchHandler.obtainMessage();
        message.what = ATTACH_WHAT;
        SyncMsg msg = new SyncMsg();
        msg.key = key;
        msg.lifecycle = lifecycle;
        message.obj = msg;
        syncSwitchHandler.sendMessage(message);
    }

    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observablePopEvent(Class key, Observable<PopupWindowEvent> observable, final GainPopLifecycle lifecycle, boolean isSync) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        if (!lifecycleSyncPopup.containsKey(key)) {
            lifecycleSyncPopup.put(key, new ArrayList<>());
        }
        if (isSync) {
            sendAttachMsg(key, lifecycle);
        } else {
            lifecycle.onGainAttach();
            addLifecycle(key, lifecycle);
        }

        bindPopLifecycleEvent(key, observable);
    }


    /**
     * 绑定pop生命周期
     * @param key
     * @param observable
     */
    private void bindPopLifecycleEvent(Class key, Observable<PopupWindowEvent> observable){
        if (lifecycleDisposables.containsKey(key)) return;
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
                                        lifecycleSyncPopup.get(key).add(PopupWindowEvent.SHOW);
                                        popLifecycle.show();
                                        break;
                                    case DISMISS:
                                        lifecycleSyncPopup.get(key).add(PopupWindowEvent.DISMISS);
                                        popLifecycle.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 执行异步绑定生命周期
     *
     * @param key
     */
    private void exePopupLifecycleEventSync(Class key, Lifecycle lifecycle) {
        List<PopupWindowEvent> popupWindowEvents = lifecycleSyncPopup.get(key);
        if (CollectionUtils.isNotEmpty(popupWindowEvents)) {
            for (int i = 0; i < popupWindowEvents.size(); i++) {
                PopupWindowEvent popupWindowEvent = popupWindowEvents.get(i);
                switch (popupWindowEvent) {
                    case SHOW:
                        Message message = syncSwitchHandler.obtainMessage();
                        message.what = POPUP_SHOW_WHAT;
                        message.obj = lifecycle;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case DISMISS:
                        Message message1 = syncSwitchHandler.obtainMessage();
                        message1.what = POPUP_DISMISS_WHAT;
                        message1.obj = lifecycle;
                        syncSwitchHandler.sendMessage(message1);
                        break;
                }
            }
        }
    }


    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableDialogEvent(Class key, Observable<DialogEvent> observable, final GainDialogLifecycle lifecycle, boolean isSync) {
        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                return;
            }
        }
        if (!lifecycleSyncDialog.containsKey(key)) {
            lifecycleSyncDialog.put(key, new ArrayList<>());
        }
        if (isSync) {
            sendAttachMsg(key, lifecycle);
        } else {
            lifecycle.onGainAttach();
            addLifecycle(key, lifecycle);
        }

        bindDialogLifecycleEvent(key, observable);

    }


    /**
     * 绑定dialog生命周期
     * @param key
     * @param observable
     */
    private void bindDialogLifecycleEvent(Class key,Observable<DialogEvent> observable){
        if (lifecycleDisposables.containsKey(key)) return;
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
                                        lifecycleSyncDialog.get(key).add(DialogEvent.START);
                                        dialogLifecycle.onStart();
                                        break;
                                    case SHOW:
                                        lifecycleSyncDialog.get(key).add(DialogEvent.SHOW);
                                        dialogLifecycle.show();
                                        break;
                                    case STOP:
                                        lifecycleSyncDialog.get(key).add(DialogEvent.STOP);
                                        dialogLifecycle.onStop();
                                        break;
                                    case DISMISS:
                                        lifecycleSyncDialog.get(key).add(DialogEvent.DISMISS);
                                        dialogLifecycle.dismiss();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }


    /**
     * 执行已经执行过的Dialog生命周期
     *
     * @param key
     * @param lifecycle
     */
    private void exeDialogLifecycleEventSync(Class key, Lifecycle lifecycle) {
        List<DialogEvent> dialogEvents = lifecycleSyncDialog.get(key);
        if (CollectionUtils.isNotEmpty(dialogEvents)) {
            Message message = syncSwitchHandler.obtainMessage();
            message.obj = lifecycle;
            for (DialogEvent event : dialogEvents) {
                switch (event) {
                    case START:
                        message.what = DIALOG_START_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case SHOW:
                        message.what = DIALOG_SHOW_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case STOP:
                        message.what = DIALOG_STOP_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case DISMISS:
                        message.what = DIALOG_DISMISS_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                }
            }
        }
    }

    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableActivityEvent(Class key, Observable<ActivityEvent> observable, final GainActivityLifecycle lifecycle, boolean isLoadAttach, boolean isSync) {
        if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
                return;
            }
        }

        if (!lifecycleSyncActivity.containsKey(key)) {
            lifecycleSyncActivity.put(key, new ArrayList<>());
        }

        if (isSync && isLoadAttach) {
            sendAttachMsg(key, lifecycle);
        } else if (isLoadAttach) {
            lifecycle.onGainAttach();
            addLifecycle(key, lifecycle);
        }

        bindActivityLifecycleEvent(key,observable);

    }

    /**
     * 绑定activity生命周期
     * @param key
     * @param observable
     */
    private void bindActivityLifecycleEvent(Class key,Observable<ActivityEvent> observable){
        if (lifecycleDisposables.containsKey(key)) return;
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
                                        lifecycleSyncActivity.get(key).add(ActivityEvent.RESUME);
                                        activityLifecycle.onResume();
                                        break;
                                    case PAUSE:
                                        lifecycleSyncActivity.get(key).add(ActivityEvent.PAUSE);
                                        activityLifecycle.onPause();
                                        break;
                                    case STOP:
                                        lifecycleSyncActivity.get(key).add(ActivityEvent.STOP);
                                        activityLifecycle.onStop();
                                        break;
                                }
                            }
                        }
                    }
                }));
    }

    /**
     * 执行activity被绑定过的生命周期
     *
     * @param key
     * @param lifecycle
     */
    private void exeActivityLifecycleEventSync(Class key, Lifecycle lifecycle) {
        List<ActivityEvent> activityEvents = lifecycleSyncActivity.get(key);
        if (CollectionUtils.isNotEmpty(activityEvents)) {
            Message message = syncSwitchHandler.obtainMessage();
            message.obj = lifecycle;
            for (ActivityEvent event : activityEvents) {
                switch (event) {
                    case RESUME:
                        message.what = ACTIVITY_RESUME_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case PAUSE:
                        message.what = ACTIVITY_PAUSE_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case STOP:
                        message.what = ACTIVITY_STOP_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                }
            }
        }
    }


    /**
     * 回调对应的生命周期
     *
     * @param observable
     * @param lifecycle
     */
    public synchronized void observableFragmentEvent(Class key, Observable<FragmentEvent> observable, final GainFragmentLifecycle lifecycle, boolean isLoadAttach, boolean isSync) {
        if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
            return;
        }
        synchronized (this) {
            if (lifecycleTags.contains(lifecycle.getClass().getName()) && lifecycles.containsKey(key)) {
                return;
            }
        }

        if (!lifecycleSyncFragment.containsKey(key)) {
            lifecycleSyncFragment.put(key, new ArrayList<>());
        }
        if (isSync && isLoadAttach) {
            sendAttachMsg(key, lifecycle);
        } else if (isLoadAttach) {
            lifecycle.onGainAttach();
            addLifecycle(key, lifecycle);
        }

        bindFragmentLifecycleEvent(key,observable);
    }


    /**
     * 绑定fragment生命周期
     *
     * @param key
     * @param observable
     */
    private void bindFragmentLifecycleEvent(Class key, Observable<FragmentEvent> observable) {
        if (lifecycleDisposables.containsKey(key)) return;
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
                                        lifecycleSyncFragment.get(key).add(FragmentEvent.RESUME);
                                        fragmentLifecycle.onResume();
                                        break;
                                    case PAUSE:
                                        lifecycleSyncFragment.get(key).add(FragmentEvent.PAUSE);
                                        fragmentLifecycle.onPause();
                                        break;
                                    case STOP:
                                        lifecycleSyncFragment.get(key).add(FragmentEvent.STOP);
                                        fragmentLifecycle.onStop();
                                        break;
                                    case DESTROY_VIEW:
                                        lifecycleSyncFragment.get(key).add(FragmentEvent.DESTROY_VIEW);
                                        fragmentLifecycle.onDestroyView();
                                        break;
                                }
                            }
                        }

                    }
                }));
    }

    /**
     * 执行异步fragment生命周期
     *
     * @param key
     * @param lifecycle
     */
    private void exeFragmentLifecycleEventSync(Class key, Lifecycle lifecycle) {
        List<FragmentEvent> events = lifecycleSyncFragment.get(key);
        if (CollectionUtils.isNotEmpty(events)) {
            Message message = syncSwitchHandler.obtainMessage();
            message.obj = lifecycle;
            for (FragmentEvent event : events) {
                switch (event) {
                    case RESUME:
                        message.what = ACTIVITY_RESUME_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case PAUSE:
                        message.what = ACTIVITY_PAUSE_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case STOP:
                        message.what = ACTIVITY_STOP_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                    case DESTROY_VIEW:
                        message.what = FRAGMENT_DESTORY_VIEW_WHAT;
                        syncSwitchHandler.sendMessage(message);
                        break;
                }
            }
        }
    }


    /**
     * 添加生命周期到容器中
     * @param key
     * @param lifecycle
     */
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


    /**
     * 解绑生命周期
     * @param key
     * @param isSync
     */
    public void unAttachLifecycle(Class key, boolean isSync) {
        if (lifecycles.containsKey(key)) {
            List<Lifecycle> baseLifecycles = lifecycles.remove(key);
            if (CollectionUtils.isNotEmpty(baseLifecycles)) {
                if (isSync) {
                    unAttachSyncLifecycle(baseLifecycles);
                } else {
                    for (int i = baseLifecycles.size() - 1; i >= 0; i--) {
                        Lifecycle lifecycle = baseLifecycles.get(i);
                        if (lifecycleTags.contains(lifecycle.getClass().getName())) {
                            lifecycleTags.remove(lifecycle.getClass().getName());
                        }
                        //调用解绑回调
                        lifecycle.onGainDetach();
                    }
                    baseLifecycles.clear();
                }
            }
        }

        /**
         * 解绑lifecycleDisposables
         */
        if (lifecycleDisposables.containsKey(key)) {
            Disposable disposable = lifecycleDisposables.remove(key);
            if (disposable != null) {
                disposable.dispose();
            }
        }

        /**
         * 解绑lifecycleSyncPopup
         */
        if (lifecycleSyncPopup.containsKey(key)) {
            List<PopupWindowEvent> popupWindowEvents = lifecycleSyncPopup.remove(key);
            if (CollectionUtils.isNotEmpty(popupWindowEvents)) {
                popupWindowEvents.clear();
            }
        }

        /**
         * 解绑lifecycleSyncDialog
         */
        if (lifecycleSyncDialog.containsKey(key)) {
            List<DialogEvent> popupWindowEvents = lifecycleSyncDialog.remove(key);
            if (CollectionUtils.isNotEmpty(popupWindowEvents)) {
                popupWindowEvents.clear();
            }
        }

        /**
         * 解绑lifecycleSyncActivity
         */
        if (lifecycleSyncActivity.containsKey(key)) {
            List<ActivityEvent> events = lifecycleSyncActivity.get(key);
            if (CollectionUtils.isNotEmpty(events)) {
                events.clear();
            }
        }

        /**
         * 解绑lifecycleSyncFragment
         */
        if (lifecycleSyncFragment.containsKey(key)) {
            List<FragmentEvent> events = lifecycleSyncFragment.get(key);
            if (CollectionUtils.isNotEmpty(events)) {
                events.clear();
            }
        }

    }


    /**
     * 异步解除生命周期绑定
     * @param baseLifecycles
     */
    private void unAttachSyncLifecycle(List<Lifecycle> baseLifecycles) {
        Observable.create(new ObservableOnSubscribe<Message>() {
            @Override
            public void subscribe(ObservableEmitter<Message> e) throws Exception {
                Message message = syncSwitchHandler.obtainMessage();
                message.what = UNTATCH_WHAT;
                for (Lifecycle baseLifecycle : baseLifecycles) {
                    message.obj = baseLifecycle;
                    e.onNext(message);
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Message>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Message message) {
                        syncSwitchHandler.sendMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        baseLifecycles.clear();
                    }
                });

    }


    class SyncMsg {
        private Lifecycle lifecycle;
        private Class key;
        private int what;
    }
}
