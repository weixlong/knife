package com.wxl.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe : 用于注解一个属性。
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD})
public @interface GainField {

    /**
     * 该Class即是该属性的实体类class，请确保在不是GainKnife绑定注册的实现中保留默认构造方法，否则初始化失败，
     * 将会在使用中出现空指针风险
     * 如您的类不可使用无参构造，或者已不为null，可使用GainKnife.registerUnableConstructorTarget注册使用
     * @return
     */
    Class target();

    /**
     * 被注解的实体类的生命周期与该class同步
     * 如在target实体类上注解了life，则同步为该注解指向的生命周期
     * 如不需要生命周期，可忽略此项
     * @return
     */
    Class life() default GainLifecycle.class;

    /**
     * 是否加载被注解的实体中包含@Gain类注解
     * @return
     */
    boolean isLoadChild() default true;

}
