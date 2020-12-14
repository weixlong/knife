package com.wxl.apt_processor.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.wxl.apt_processor.proxy.GainTypeClassCreatorProxy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe : 处理GainType注解
 */
public class GainTypeProcessor extends AbsProcessor {

    private List<GainTypeClassCreatorProxy> proxies = new ArrayList<>();

    @Override
    public void createJavaFile(Elements mElements, ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
//        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GainType.class);//获得被GainType注解标记的element
////        for (Element element : elements) {
////            GainTypeClassCreatorProxy proxy = new GainTypeClassCreatorProxy();
////            proxy.load(processingEnv.getElementUtils(),processingEnv,element);
////            proxies.add(proxy);
////        }
////        writeJava(processingEnv);
    }

    @Override
    protected void writeJava(ProcessingEnvironment processingEnv) {
        for (GainTypeClassCreatorProxy proxy : proxies) {
            TypeSpec typeSpec = proxy.generateJavaCode();
            if (proxy.getPackageName() != null && !proxy.getPackageName().isEmpty() && typeSpec != null)
                try {
                    JavaFile javaFile = JavaFile.builder(proxy.getPackageName(), typeSpec).build();
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
