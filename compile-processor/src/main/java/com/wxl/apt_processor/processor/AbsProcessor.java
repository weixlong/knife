package com.wxl.apt_processor.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.wxl.apt_processor.proxy.ClassCreatorProxy;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public abstract class AbsProcessor {

    protected HashMap<String, ClassCreatorProxy> mProxyMap = new HashMap<>();

    public abstract void createJavaFile(Elements mElements, ProcessingEnvironment processingEnv, RoundEnvironment roundEnv);

    public HashMap<String, ClassCreatorProxy> getProxyMap() {
        return mProxyMap;
    }

    protected void writeJava(ProcessingEnvironment processingEnv){
        for (String key : mProxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = mProxyMap.get(key);
            if(proxyInfo != null) {
                TypeSpec typeSpec = proxyInfo.generateJavaCode();
                if (proxyInfo.getPackageName() != null && !proxyInfo.getPackageName().isEmpty() && typeSpec != null)
                    try {
                        JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), typeSpec).build();
                        javaFile.writeTo(processingEnv.getFiler());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
