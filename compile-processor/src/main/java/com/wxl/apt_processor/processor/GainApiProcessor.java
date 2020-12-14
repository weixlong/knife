package com.wxl.apt_processor.processor;

import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_processor.proxy.GainApiClassCreatorProxy;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe :
 */
public class GainApiProcessor extends AbsProcessor{
    @Override
    public void createJavaFile(Elements mElements, ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GainApi.class);//获得被GainField注解标记的element
        for (Element element : elements) {
            if(!getProxyMap().containsKey(GainApi.class.getName())){
                getProxyMap().put(GainApi.class.getName(),new GainApiClassCreatorProxy());
            }
            getProxyMap().get(GainApi.class.getName()).load(processingEnv.getElementUtils(),processingEnv,element);
        }
        GainApiClassCreatorProxy.writeJava(processingEnv);
    }
}
