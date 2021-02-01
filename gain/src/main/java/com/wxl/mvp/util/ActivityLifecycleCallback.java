package com.wxl.mvp.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.wxl.gainjet.Loog;
import com.wxl.mvp.lifecycle.AppLifecycle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * create file time : 2020/10/16
 * create user : wxl
 * subscribe :
 */
public class ActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    private int countActivity = 0;
    //是否进入后台
    private boolean isBackground = false;

    private String TAG = "ActivityLifecycleCallback";

    //为避免内存泄漏使用弱引用
    private static WeakReference<Activity> mCurrentActivity;

    private static List<AppLifecycle> appLifecycles = new ArrayList<>();


    public static Activity getCurrentActivity(){
        return mCurrentActivity.get();
    }

    private static void setCurrentActivity(Activity activity){
        mCurrentActivity = new WeakReference<>(activity);
    }

    public static void registerAppRunningLifecycle(AppLifecycle lifecycle){
        if(lifecycle != null){
            appLifecycles.add(lifecycle);
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        ActivityLifecycleCallback.setCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        countActivity++;
        if (countActivity == 1 && isBackground) {
            Loog.e(TAG, "onActivityStarted: 应用进入前台");
            isBackground = false;
            //说明应用重新进入了前台
            loadLifeLine();
        }
    }


    private void loadLifeLine(){
        for (AppLifecycle lifecycle : appLifecycles) {
            if(lifecycle != null){
                lifecycle.onFront();
            }
        }
    }


    private void loadOffLine(){
        for (AppLifecycle lifecycle : appLifecycles) {
            if(lifecycle != null){
                lifecycle.onBackground();
            }
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        ActivityLifecycleCallback.setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        countActivity--;
        if (countActivity <= 0 && !isBackground && isRun(activity)) {
            Loog.e(TAG, "onActivityStarted: 应用进入后台");
            isBackground = true;
            //说明应用进入了后台
            loadOffLine();
        }

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    /**
     * 判断应用是否在运行
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean isRun(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        boolean isAppRunning = false;
        String MY_PKG_NAME = context.getPackageName();
        //100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表系统后台有此进程在运行
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                isAppRunning = true;
                Loog.i(TAG, info.topActivity.getPackageName() + " info.baseActivity.getPackageName()=" + info.baseActivity.getPackageName());
                break;
            }
        }

        Loog.i(TAG, "com.ad 程序   ...isAppRunning......" + isAppRunning);
        return isAppRunning;
    }
}
