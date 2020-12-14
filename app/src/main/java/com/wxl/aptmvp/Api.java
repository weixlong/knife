package com.wxl.aptmvp;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * create file time : 2020/12/8
 * create user : wxl
 * subscribe :
 */
public interface Api {

    @POST("/")
    @FormUrlEncoded
    Observable<String> loadConfig(@Field("service") String service);
}
