package com.wxl.apt_processor.processor;

import com.wxl.apt_annotation.GainLifecycle;
import com.wxl.apt_processor.proxy.GainHttpLifecycleClassCreatorProxy;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/12/8
 * create user : wxl
 * subscribe :
 */
public class LifecycleProcessor extends AbsProcessor {

    @Override
    public void createJavaFile(Elements mElements, ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GainLifecycle.class);//获得被GainField注解标记的element
        for (Element element : elements) {
            if(!getProxyMap().containsKey(GainLifecycle.class.getName())){
                getProxyMap().put(GainLifecycle.class.getName(),new GainHttpLifecycleClassCreatorProxy());
            }
            getProxyMap().get(GainLifecycle.class.getName()).load(processingEnv.getElementUtils(),processingEnv,element);
        }
        GainHttpLifecycleClassCreatorProxy.writeJava(processingEnv);
    }
}
