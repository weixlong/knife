package com.wxl.apt_processor.processor;

import com.google.auto.service.AutoService;
import com.wxl.apt_annotation.GainApi;
import com.wxl.apt_annotation.GainField;
import com.wxl.apt_annotation.GainLifecycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
@SupportedAnnotationTypes({"com.wxl.apt_annotation.GainLifecycle", "com.wxl.apt_annotation.GainField",
        "com.wxl.apt_annotation.GainApi"})
@AutoService(Processor.class)
public class MvpBindProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Elements mElementUtils;
    private Map<Class<?>, AbsProcessor> processors = new HashMap<>();
    private Class<?>[] annotationCls = new Class[]{GainLifecycle.class, GainField.class, GainApi.class};


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        for (int i = 0; i < annotationCls.length; i++) {
            supportTypes.add(annotationCls[i].getCanonicalName());
        }
        processors.put(GainLifecycle.class, new LifecycleProcessor());
        processors.put(GainField.class, new GainFieldProcessor());
        processors.put(GainApi.class,new GainApiProcessor());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        for (Class<?> cl : annotationCls) {
            AbsProcessor pProcessor = processors.get(cl);
            if (pProcessor != null) {
                pProcessor.createJavaFile(mElementUtils,processingEnv, roundEnv);
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process finish ...");
        return true;
    }
}
