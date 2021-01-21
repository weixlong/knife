package com.wxl.mvp;

import android.app.Application;
import android.content.Context;

import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.lifecycle.AppLifecycle;
import com.wxl.mvp.util.ActivityLifecycleCallback;
import com.wxl.mvp.util.Tu;
import com.wxl.mvp.util.XmlDB;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe :
 */
public class GainNote {


    private static Context context;
    private static boolean debug = false;

    /**
     * 初始化
     * @param context 上下文
     *
     */
    public static void init(Context context, GainHttp.Option... option){
        init(context,debug,null,option);
    }

    /**
     * 初始化
     * @param context 上下文
     * @param debug 是否打开调试模式
     */
    public static void init(Context context,boolean debug,GainHttp.Option... option){
       init(context,debug,null,option);
    }

    /**
     * 初始化
     * @param context 上下文
     * @param debug 是否打开调试模式
     * @param lifecycle APP 前后台回调
     */
    public static void init(Context context,boolean debug,AppLifecycle lifecycle,GainHttp.Option... options){
        GainNote.context = context;
        GainNote.debug = debug;
        buildGainHttpOptions(options);
        addApplicationActivityCallback(lifecycle);
        Tu.initialize(context);
        XmlDB.initialize((Application) context.getApplicationContext());
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
     * 构建GainHttpOption
     * @param options
     */
    private static void buildGainHttpOptions(GainHttp.Option... options){
        if(options != null && options.length > 0){
            for (GainHttp.Option option : options) {
                option.setDebug(debug).build();
            }
        }
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
