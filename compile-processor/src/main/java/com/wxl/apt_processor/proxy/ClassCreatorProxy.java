package com.wxl.apt_processor.proxy;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public abstract class ClassCreatorProxy {

    protected Elements mElementUtils;
    protected Element mElement;
    protected String mBindingClassName;
    protected String mPackageName;
    protected Messager mMessager;
    protected String classQualifiedName;

    public ClassCreatorProxy() {

    }


    public void load(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element){
        this.mElement = element;
        this.mElementUtils = mElementUtils;
        mMessager = processingEnv.getMessager();
        PackageElement packageElement = mElementUtils.getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();
        classQualifiedName = getQualifiedName();
        this.mPackageName = packageName;
        this.mBindingClassName = getBindingClassName();
    }

    public abstract TypeSpec generateJavaCode();

    protected abstract MethodSpec generateMethods();

    public String getPackageName() {
        return mPackageName;
    }


    public abstract String getQualifiedName();


    public abstract String getBindingClassName();

}
