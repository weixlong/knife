package com.wxl.mvp.http;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Copyright：贵州玄机科技有限公司
 * Created by wxl on 2020/7/15 16:45
 * Description：
 * Modify time：
 * Modified by：
 */
public class AccessTokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        //获取原先的请求对象
        Request request = chain.request();
        request = addGetBaseParams(request);

        return chain.proceed(request);
    }

    /**
     * 添加GET方法基础参数
     *
     * @param request
     * @return
     */
    private Request addGetBaseParams(Request request) {

        HttpUrl.Builder builder = request.url()
                .newBuilder();

//        if (SpUtil.hasToken()) {
//            builder.addQueryParameter("token", SpUtil.token())//平台号
//                    .addQueryParameter("uid", SpUtil.getUserId());
//        }

        HttpUrl httpUrl = builder.build();

//        request = request.newBuilder()
//                .addHeader("Connection", "keep-alive")
//                .addHeader("referer", CommonAppConfig.HOST_API_PUBLIC)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("token", SpUtil.hasToken() ? SpUtil.token() : "")
//                .addHeader("uid", SpUtil.hasToken() ? SpUtil.getUserId() : "0")
//                .url(httpUrl)
//                .build();

        return request;
    }

}
