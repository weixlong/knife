package com.wxl.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create file time : 2020/12/8
 * create user : wxl
 * subscribe : 注解方法上时 只针对http请求的生命周期，注解在类上时表示该类下使用的http请求生命周期都按照该注解结束，
 * 如类和方法同时注解，则会优先选择方法注解使用
 * 同一个类里面不能具有相同方法名且同时注解GainLifecycle ，否则只有第一个注解的方法生效
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface GainLifecycle {
    /**
     * 生命周期指向，指向一个已经被注册的类，该类需要具有生命周期能力，否则将不会生效。
     * @return
     */
    Class life();


    /**
     * 在该生命周期时结束所有的请求
     * @return
     */
    ApiEvent event() default ApiEvent.DESTROY;
}
