package com.wxl.mvp.http;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.util.navbar.NavigationBarObserver;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;
import com.wxl.apt_annotation.ApiEvent;
import com.wxl.mvp.GainNote;
import com.wxl.mvp.http.convert.FastJsonConverterFactory;
import com.wxl.mvp.http.convert.ToStringConverterFactory;
import com.wxl.mvp.http.exception.ExceptionHandler;
import com.wxl.mvp.http.trust.SSLSocketClient;
import com.wxl.mvp.knife.Knife;
import com.wxl.mvp.knife.LifecycleBean;
import com.wxl.mvp.smart.SmartView;
import com.wxl.mvp.util.ActivityLifecycleCallback;
import com.wxl.mvp.util.Loog;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wxl on 2019/6/26.
 * https://github.com/QMUI/QMUI_Android
 */

public class GainHttp {

    /**
     * 静态内部类保证全局唯一
     */
    private static class Instance {
        private static Option option = new Option();
        private static GainHttp request = new GainHttp();
        private static HashMap<Class, Object> api = new HashMap<>();
    }


    /**
     * 请求接口
     *
     * @return Api
     */
    public static <Api> Api api(Class<Api> apiClass) {
        return (Api) Instance.api.get(apiClass);
    }



    /**
     * 默认执行
     *
     * @param observable
     * @param callback
     */
    public static <T> void exe(Observable<T> observable, Callback<T> callback) {
        HttpLifecycleUser callClass = getCallClass();
        if(callClass != null) {
            LifecycleBean lifecycleProvider = Knife.findHttpClassMethodLifecycleAnnotation(callClass);
            if (lifecycleProvider != null) {
                exe(lifecycleProvider.getLifecycleProvider(), observable, callback,lifecycleProvider.getEvent());
            } else {
                observable.compose(GainHttp.get().defaultSchedulers())
                        .subscribe(GainHttp.get().getSimpleSubscriber(callback));
            }
        } else {
            observable.compose(GainHttp.get().defaultSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行
     *
     * @param observable
     * @param callback
     */
    public static <T> void exe(Observable<T> observable, Callback<T> callback, ApiEvent event) {
        HttpLifecycleUser callClass = getCallClass();
        if(callClass != null) {
            LifecycleBean lifecycleProvider = Knife.findHttpClassMethodLifecycleAnnotation(callClass);
            if (lifecycleProvider != null) {
                exe(lifecycleProvider.getLifecycleProvider(), observable, callback, event);
            } else {
                observable.compose(GainHttp.get().defaultSchedulers())
                        .subscribe(GainHttp.get().getSimpleSubscriber(callback));
            }
        } else {
            observable.compose(GainHttp.get().defaultSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行
     *
     * @param observable
     * @param callback
     */
    public static <T> void exe(LifecycleProvider lifecycleProvider, Observable<T> observable, Callback<T> callback) {
        if (lifecycleProvider != null) {
            observable.compose(GainHttp.get().defaultSchedulers(lifecycleProvider))
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        } else {
            observable.compose(GainHttp.get().defaultSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行
     *
     * @param observable
     * @param callback
     */
    public static <T> void exe(LifecycleProvider lifecycleProvider, Observable<T> observable, Callback<T> callback, ApiEvent event) {
        if (lifecycleProvider != null) {
            observable.compose(GainHttp.get().defaultSchedulers(lifecycleProvider, event))
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        } else {
            observable.compose(GainHttp.get().defaultSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行,带对话框
     * ActivityManager 里要设置当前的Activity
     *
     * @param observable
     * @param callback
     */
    public static <T> void load(Observable<T> observable, Callback<T> callback) {
        HttpLifecycleUser callClass = getCallClass();
        if(callClass != null) {
            LifecycleBean lifecycleProvider = Knife.findHttpClassMethodLifecycleAnnotation(callClass);
            if (lifecycleProvider != null) {
                load(lifecycleProvider.getLifecycleProvider(), observable, callback,lifecycleProvider.getEvent());
            } else {
                observable.compose(GainHttp.get().defaultDialogSchedulers())
                        .subscribe(GainHttp.get().getSimpleSubscriber(callback));
            }
        } else {
            observable.compose(GainHttp.get().defaultDialogSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行,带对话框
     * ActivityManager 里要设置当前的Activity
     *
     * @param observable
     * @param callback
     */
    public static <T> void load(Observable<T> observable, Callback<T> callback, ApiEvent event) {
        HttpLifecycleUser callClass = getCallClass();
        if(callClass != null) {
            LifecycleBean lifecycleProvider = Knife.findHttpClassMethodLifecycleAnnotation(callClass);
            if (lifecycleProvider != null) {
                load(lifecycleProvider.getLifecycleProvider(), observable, callback, event);
            } else {
                observable.compose(GainHttp.get().defaultDialogSchedulers())
                        .subscribe(GainHttp.get().getSimpleSubscriber(callback));
            }
        } else {
            observable.compose(GainHttp.get().defaultDialogSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行,带对话框
     * ActivityManager 里要设置当前的Activity
     *
     * @param observable
     * @param callback
     */
    public static <T> void load(LifecycleProvider lifecycleProvider, Observable<T> observable, Callback<T> callback) {
        if (lifecycleProvider != null) {
            observable.compose(GainHttp.get().defaultDialogSchedulers(lifecycleProvider))
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        } else {
            observable.compose(GainHttp.get().defaultDialogSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认执行,带对话框
     * ActivityManager 里要设置当前的Activity
     *
     * @param observable
     * @param callback
     */
    public static <T> void load(LifecycleProvider lifecycleProvider, Observable<T> observable, Callback<T> callback, ApiEvent event) {
        if (lifecycleProvider != null) {
            observable.compose(GainHttp.get().defaultDialogSchedulers(lifecycleProvider, event))
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        } else {
            observable.compose(GainHttp.get().defaultDialogSchedulers())
                    .subscribe(GainHttp.get().getSimpleSubscriber(callback));
        }
    }


    /**
     * 默认描述者
     *
     * @param callback
     * @return DefaultSubscriber
     */
    public <T> DefaultSubscriber<T> getDefaultSubscriber(Callback<T> callback) {
        return DefaultSubscriber.newInstance(callback);
    }

    /**
     * 返回简单描述者
     *
     * @param callback
     * @param <T>
     * @return
     */
    public <T> SimpleSubscriber<T> getSimpleSubscriber(Callback<T> callback) {
        return SimpleSubscriber.newInstance(callback);
    }

    public static class Option<Api> {

        private static int HTTP_CONNECT_OUT_TIME = 10;

        private static String BASE_URL = "";

        private static int SUCCESS_CODE = 0;

        private OkHttpClient.Builder builder;

        private Class<Api> apiClass;

        private ExceptionHandler.OnExceptionCallback exceptionCallback;

        private LoadingPopupView popupView;

        private ActivityEvent event = ActivityEvent.DESTROY;

        private FragmentEvent fragmentEvent = FragmentEvent.DESTROY;

        private Option() {
            if (builder == null) {
                builder = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .sslSocketFactory(SSLSocketClient.getSSLSocketFactory())
                        .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                        .connectTimeout(HTTP_CONNECT_OUT_TIME, TimeUnit.SECONDS)
                        .writeTimeout(HTTP_CONNECT_OUT_TIME, TimeUnit.SECONDS)
                        .readTimeout(HTTP_CONNECT_OUT_TIME, TimeUnit.SECONDS);
                if (GainNote.isDebug()) {
                    builder.addInterceptor(new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY));
                }
            }
        }


        /**
         * 添加拦截器<p>
         * ● 不需要担心中间过程的响应,如重定向和重试.<p>
         * ● 总是只调用一次,即使HTTP响应是从缓存中获取.<p>
         * ● 观察应用程序的初衷. 不关心OkHttp注入的头信息如: If-None-Match.<p>
         * ● 允许短路而不调用 Chain.proceed(),即中止调用.<p>
         * ● 允许重试,使 Chain.proceed()调用多次.<p>
         *
         * @param interceptor
         * @return Option
         */
        public Option addInterceptor(Interceptor interceptor) {
            builder.addInterceptor(interceptor);
            return this;
        }

        /**
         * 添加Smart拦截器<p>
         * ● 不需要担心中间过程的响应,如重定向和重试.<p>
         * ● 总是只调用一次,即使HTTP响应是从缓存中获取.<p>
         * ● 观察应用程序的初衷. 不关心OkHttp注入的头信息如: If-None-Match.<p>
         * ● 允许短路而不调用 Chain.proceed(),即中止调用.<p>
         * ● 允许重试,使 Chain.proceed()调用多次.<p>
         *
         * @param interceptor
         * @return Option
         */
        public Option addSmartInterceptor(Interceptor interceptor){
            SmartView.option().addInterceptor(interceptor);
            return this;
        }


        /**
         * 添加网络拦截器<p>
         * ● 能够操作中间过程的响应,如重定向和重试.<p>
         * ● 当网络短路而返回缓存响应时不被调用.<p>
         * ● 只观察在网络上传输的数据.<p>
         * ● 携带请求来访问连接.<p>
         *
         * @param interceptor
         * @return Option
         */
        public Option addNetworkInterceptor(Interceptor interceptor) {
            builder.addNetworkInterceptor(interceptor);
            SmartView.option().addNetworkInterceptor(interceptor);
            return this;
        }


        /**
         * 添加Smart网络拦截器<p>
         * ● 能够操作中间过程的响应,如重定向和重试.<p>
         * ● 当网络短路而返回缓存响应时不被调用.<p>
         * ● 只观察在网络上传输的数据.<p>
         * ● 携带请求来访问连接.<p>
         *
         * @param interceptor
         * @return Option
         */
        public Option addSmartNetworkInterceptor(Interceptor interceptor) {
            SmartView.option().addNetworkInterceptor(interceptor);
            return this;
        }

        /**
         * 设置取消请求的生命周期
         */
        public Option setClientCancelEvent(ActivityEvent event) {
            this.event = event;
            return this;
        }

        /**
         * 设置取消请求的生命周期
         *
         * @param fragmentEvent
         * @return
         */
        public Option setClientCancelEvent(FragmentEvent fragmentEvent) {
            this.fragmentEvent = fragmentEvent;
            return this;
        }

        /**
         * 错误回调
         *
         * @param exceptionCallback
         * @return Option
         */
        public Option setExceptionInterceptor(ExceptionHandler.OnExceptionCallback exceptionCallback) {
            this.exceptionCallback = exceptionCallback;
            return this;
        }

        /**
         * 设置接口类
         * 可在MVP层中GainApi注解使用该api
         * 也可Gain.api() 调用
         *
         * @param apiClass
         * @return Option
         */
        public Option api(Class<Api> apiClass) {
            this.apiClass = apiClass;
            return this;
        }

        /**
         * 创建retrofit
         *
         * @param clazz
         * @param baseUrl
         * @param <T>
         * @return T
         */
        private <T> T createApi(Class<T> clazz, String baseUrl) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(builder.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(ToStringConverterFactory.create())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())//添加Gson支持，然后Retrofit就会使用Gson将响应体（api接口的Take）转换我们想要的类型。
                    .build();
            return retrofit.create(clazz);
        }


        /**
         * 构建Retrofit
         */
        public void build() {
            Loog.methodE("put api : "+apiClass.getName());
            Instance.api.put(apiClass, createApi(apiClass, BASE_URL));
            ExceptionHandler.exceptionCallback(exceptionCallback);
        }


        /**
         * 显示加载中
         */
        private void showLoading() {
            Activity activity = ActivityLifecycleCallback.getCurrentActivity();
            if (popupView == null && activity != null) {
                XPopup.Builder builder = new XPopup.Builder(activity).hasShadowBg(false);
                popupView = builder.asLoading("加载中");
            }
            if (popupView != null && !popupView.isShow()) {
                popupView.show();
            }
        }

        /**
         * 关闭加载中
         */
        private void dismissLoading() {
            if (popupView != null) {
                popupView.dismiss();
                NavigationBarObserver.getInstance().removeOnNavigationBarListener(popupView);
                popupView = null;
            }
        }


        /**
         * 成功码 默认为0
         *
         * @param code
         * @return Option
         */
        public Option okCode(int code) {
            SUCCESS_CODE = code;
            return this;
        }

        /**
         * 超时时间
         *
         * @param outSecondTime
         * @return
         */
        public Option setConnectOutTime(int outSecondTime) {
            Option.HTTP_CONNECT_OUT_TIME = outSecondTime;
            return this;
        }


        /**
         * 基本路径
         *
         * @param baseUrl
         * @return Option
         */
        public Option baseUrl(String baseUrl) {
            Option.BASE_URL = baseUrl;
            return this;
        }


        /**
         * 获取超时时间
         *
         * @return ConnectOutTime
         */
        public static int getHttpConnectOutTime() {
            return HTTP_CONNECT_OUT_TIME;
        }


        /**
         * 获取错误回调
         *
         * @return OnExceptionCallback
         */
        public ExceptionHandler.OnExceptionCallback getExceptionCallback() {
            return exceptionCallback;
        }


        /**
         * 获取成功码
         *
         * @return SuccessCode
         */
        public static int getSuccessCode() {
            return SUCCESS_CODE;
        }
    }


    /**
     * 请求
     *
     * @return Gain
     */
    private static GainHttp get() {
        return Instance.request;
    }


    /**
     * 配置
     *
     * @return Option
     */
    public static Option option() {
        return Instance.option;
    }


    /**
     * 获得默认Observable
     *
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultSchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 获得默认Observable
     *
     * @param lifecycleProvider
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultSchedulers(@NonNull LifecycleProvider lifecycleProvider, ApiEvent event) {
        if (lifecycleProvider instanceof RxFragmentActivity) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(event));
        } else if (lifecycleProvider instanceof RxFragment) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(event));
        }
        return defaultSchedulers();
    }

    /**
     * 获得默认Observable
     *
     * @param lifecycleProvider
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultSchedulers(@NonNull LifecycleProvider lifecycleProvider) {
        if (lifecycleProvider instanceof RxFragmentActivity) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .compose(lifecycleProvider.bindUntilEvent(Instance.option.event))
                    .observeOn(AndroidSchedulers.mainThread());
        } else if (lifecycleProvider instanceof RxFragment) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .compose(lifecycleProvider.bindUntilEvent(Instance.option.fragmentEvent))
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return defaultSchedulers();
    }

    /**
     * 获得默认Dialog Observable
     *
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultDialogSchedulers() {
        return observable -> observable
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe(disposable -> {
                    option().showLoading();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally((Action) () -> {
                    option().dismissLoading();
                });
    }


    /**
     * 获得默认Dialog Observable
     *
     * @param lifecycleProvider
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultDialogSchedulers(@NonNull final LifecycleProvider lifecycleProvider, ApiEvent event) {
        if (lifecycleProvider instanceof RxFragmentActivity) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(event))
                    .doOnSubscribe((Consumer) disposable -> option().showLoading())
                    .doFinally((Action) () -> {
                        option().dismissLoading();
                    });
        } else if (lifecycleProvider instanceof RxFragment) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(event))
                    .doOnSubscribe(disposable -> {
                        option().showLoading();
                    })
                    .doFinally((Action) () -> {
                        option().dismissLoading();
                    });
        }
        return defaultDialogSchedulers();
    }


    /**
     * 获得默认Dialog Observable
     *
     * @param lifecycleProvider
     * @return ObservableTransformer
     */
    public <T> ObservableTransformer<T, T> defaultDialogSchedulers(@NonNull final LifecycleProvider lifecycleProvider) {
        if (lifecycleProvider instanceof RxFragmentActivity) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(Instance.option.event))
                    .doOnSubscribe((Consumer) disposable -> option().showLoading())
                    .doFinally((Action) () -> {
                        option().dismissLoading();
                    });
        } else if (lifecycleProvider instanceof RxFragment) {
            return observable -> observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(lifecycleProvider.bindUntilEvent(Instance.option.fragmentEvent))
                    .doOnSubscribe(disposable -> {
                        option().showLoading();
                    })
                    .doFinally((Action) () -> {
                        option().dismissLoading();
                    });
        }
        return defaultDialogSchedulers();
    }




    /**
     * 获得model层的类名，即默认调用请求的层级
     *
     * @return Class
     */
    public static HttpLifecycleUser getCallClass() {
        try {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            HttpLifecycleUser user = new HttpLifecycleUser(Class.forName(className),methodName);
            Loog.d(" use gain class = " + className);
            return user;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
