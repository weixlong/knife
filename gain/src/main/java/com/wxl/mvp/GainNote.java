package com.wxl.mvp;

import android.app.Application;
import android.content.Context;

import com.wxl.mvp.lifecycle.AppLifecycle;
import com.wxl.mvp.util.ActivityLifecycleCallback;
import com.wxl.mvp.util.Tu;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe :
 */
public class GainNote {


    private static Context context;
    private static boolean debug = true;

    /**
     * 初始化
     * @param context 上下文
     * @param debug 是否打开调试模式
     */
    public static void init(Context context,boolean debug){
       init(context,null,debug);
    }

    /**
     * 初始化
     * @param context 上下文
     * @param debug 是否打开调试模式
     * @param lifecycle APP 前后台回调
     */
    public static void init(Context context, AppLifecycle lifecycle, boolean debug){
        GainNote.context = context;
        addApplicationActivityCallback(lifecycle);
        GainNote.debug = debug;
        Tu.initialize(context);
    }

    /**
     * 获取上下文
     */
    public static Context getContext(){
        return context;
    }

    /**
     * 获取调试是否打开
     * @return
     */
    public static boolean isDebug() {
        return debug;
    }


    /**
     * 添加回调
     */
    private static void addApplicationActivityCallback(AppLifecycle lifecycle){
        if(context != null){
            Application application = (Application) context.getApplicationContext();
            ActivityLifecycleCallback.registerAppRunningLifecycle(lifecycle);
            application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
        }
    }
}