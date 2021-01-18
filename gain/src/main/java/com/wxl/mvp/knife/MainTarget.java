package com.wxl.mvp.knife;

import com.wxl.mvp.util.Loog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.reactivex.functions.Consumer;

/**
 * create file time : 2021/1/18
 * create user : wxl
 * subscribe :
 */
public class MainTarget {


    private MainTarget() {
    }


    private static class MT {
        private static MainTarget mt = new MainTarget();
        private static Consumer<Object> consumer;
        private static Consumer<Object> superConsumer;
    }

    public static MainTarget getInstance() {
        return MT.mt;
    }

    public Consumer<Object> getConsumer() {
        return MT.consumer;
    }

    public Consumer<Object> getSuperConsumer() {
        return MT.superConsumer;
    }

    /**
     * new object在主线程中
     */
    private class NewObjectMainThreadBean {
        private Consumer<Object> consumer;
        private Class targetCls;
    }


    /**
     * 切换到子线程
     */
    private class ObjectInThread {
        private Consumer<Object> consumer;
        private Object target;
    }

    /**
     * 切换到主线程new object
     *
     * @param consumer
     * @param targetCls
     */
    public void takeNewObjectMain(Consumer<Object> consumer, Class targetCls, boolean isSync) {
        try {
            Object o = targetCls.newInstance();
            ObjectInThread objectInThread = new ObjectInThread();
            objectInThread.consumer = consumer;
            objectInThread.target = o;
            doNextObjectThread(objectInThread);
        } catch (IllegalAccessException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InstantiationException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
    }



    /**
     * 切换回子线程继续下一步操作
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void doNextObjectThread(ObjectInThread bean) {
        if (bean.target != null && bean.consumer != null) {
            try {
                bean.consumer.accept(bean.target);
            } catch (Exception e) {
                if (Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            }
        }
    }

}
