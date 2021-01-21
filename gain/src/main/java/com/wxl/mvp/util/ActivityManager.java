package com.wxl.mvp.util;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActivityManager {

    private static List<Activity> mActivityStack;    //Activity栈

    private static class I {
        private static ActivityManager m = new ActivityManager();
    }

    private ActivityManager() {
        mActivityStack = new ArrayList<>();
    }

    public static ActivityManager getInstance() {
        return I.m;
    }

    /**
     * 添加一个activity到栈顶.
     *
     * @param activity 添加的activity
     */
    public void pushActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    /**
     * 获取栈顶的Activity.
     *
     * @return 如果栈存在, 返回栈顶的activity
     */
    public Activity peekActivity() {
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            return mActivityStack.get(mActivityStack.size()-1);
        } else {
            return null;
        }
    }

    /**
     * 结束当前栈顶的activity,在activity的onDestroy中调用.
     */
    public void popActivity() {
        if (mActivityStack != null && !mActivityStack.isEmpty()) {
            mActivityStack.remove(mActivityStack.size()-1);
        }
    }

    /**
     * 结束最接近栈顶的匹配类名的activity.
     * 遍历到的不一定是被结束的,遍历是从栈底开始查找,为了确定栈中有这个activity,并获得一个引用
     * 删除是从栈顶查找,结束查找到的第一个
     * 在activity外结束activity时调用
     *
     * @param klass 类名
     */
    public void popActivity(Class<? extends Activity> klass) {
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null && activity.getClass().equals(klass)) {
                iterator.remove();   //注意这个地方
                activity.finish();
                break;
            }
        }

    }


    public Activity findActivity(Class<? extends Activity> klass) {
        Iterator<Activity> iterator = mActivityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = iterator.next();
            if (activity != null && activity.getClass().equals(klass)) {
                return activity;
            }
        }
        return null;
    }


    public boolean cantains(Class<? extends Activity> klass) {
        for (Activity activity : mActivityStack) {
            if (activity != null && activity.getClass().equals(klass)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 结束最接近栈顶的匹配类名的activity.
     * 遍历到的不一定是被结束的,遍历是从栈底开始查找,为了确定栈中有这个activity,并获得一个引用
     * 删除是从栈顶查找,结束查找到的第一个
     * 在activity外结束activity时调用
     *
     * @param klass 类名
     */
    public void popActivitys(Class<? extends Activity>... klass) {
        for (Class<? extends Activity> aClass : klass) {
            for (Activity activity : mActivityStack) {
                if (activity != null && activity.getClass().equals(aClass)) {
                    mActivityStack.remove(activity);
                    activity.finish();
                    break;              //只结束一个
                }
            }
        }
    }

    /**
     * 关闭所有的页面并打开一个新页面
     *
     * @param aclass
     */
    public void popAllToActivity(Class<? extends Activity> aclass) {
        int size = mActivityStack.size();
        for (int i = 0; i < size; i++) {
            Activity pop = mActivityStack.get(i);
            if (pop != null) {
                if (size - 1 == i) {
                    pop.startActivity(new Intent(pop, aclass));
                }
                pop.finish();
            }
        }
    }

    //移除所有的Activity
    public void removeAll() {
        int size = mActivityStack.size();
        for (int i = 0; i < size; i++) {
            Activity pop = mActivityStack.get(i);
            if (pop != null && !pop.isFinishing()) {
                pop.finish();
            }
        }
    }


    public void exit() {
        removeAll();
        System.exit(0);
    }

}
