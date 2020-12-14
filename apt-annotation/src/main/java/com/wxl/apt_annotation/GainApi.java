package com.wxl.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe : Http Api
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface GainApi {

}
