package com.wxl.apt_processor.proxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wxl.apt_annotation.ApiEvent;
import com.wxl.apt_annotation.GainLifecycle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe :
 */
public class GainHttpLifecycleClassCreatorProxy extends ClassCreatorProxy {

    private  List<String> names = new ArrayList<>();
    private  List<String> keys = new ArrayList<>();
    private  List<ApiEvent> events = new ArrayList<>();
    private static HashMap<String, GainHttpLifecycleClassCreatorProxy> cache = new HashMap<>();

    @Override
    public void load(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        if (!cache.containsKey(getBindingClassName())) {
            GainHttpLifecycleClassCreatorProxy proxy = new GainHttpLifecycleClassCreatorProxy();
            proxy.start(mElementUtils, processingEnv, element);
            cache.put(getBindingClassName(),proxy);
        } else {
            GainHttpLifecycleClassCreatorProxy proxy = cache.get(getBindingClassName());
            proxy.start(mElementUtils, processingEnv, element);
        }

    }

    private void init(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        this.mElement = element;
        this.mElementUtils = mElementUtils;
        mMessager = processingEnv.getMessager();
        PackageElement packageElement = mElementUtils.getPackageOf(element);
        String packageName = packageElement.getQualifiedName().toString();
        classQualifiedName = getQualifiedName();
        this.mPackageName = packageName;
        this.mBindingClassName = getBindingClassName();
    }

    private void start(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        GainLifecycle gainApi = mElement.getAnnotation(GainLifecycle.class);
        if(gainApi != null) {
            ApiEvent event = gainApi.event();
            try {
                gainApi.life();
            } catch (MirroredTypeException e){
                if(!names.contains(getQualifiedName())){
                    names.add(getQualifiedName());
                    keys.add(e.getTypeMirror().toString());
                    events.add(event);
                }
            }
        }
    }


    public static void writeJava(ProcessingEnvironment processingEnv) {
       // processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"cache : "+cache.size());
        Iterator<Map.Entry<String, GainHttpLifecycleClassCreatorProxy>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, GainHttpLifecycleClassCreatorProxy> next = iterator.next();
            GainHttpLifecycleClassCreatorProxy proxy = next.getValue();
            TypeSpec typeSpec = proxy.generateJavaCode();
          //  processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, proxy.getBindingClassName());
            if (proxy.getPackageName() != null && !proxy.getPackageName().isEmpty() && typeSpec != null) {
                try {
                    JavaFile javaFile = JavaFile.builder(proxy.getPackageName(), typeSpec).build();
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        cache.clear();
    }

    @Override
    public TypeSpec generateJavaCode() {

        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();

        FieldSpec targetIds = FieldSpec.builder(ArrayList.class, "keys")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(spec)
                .build();

        FieldSpec lifes = FieldSpec.builder(ArrayList.class, "events")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(spec)
                .build();

        FieldSpec names = FieldSpec.builder(ArrayList.class, "names")
                .addModifiers(Modifier.PRIVATE)
                .addAnnotation(spec)
                .build();

        TypeSpec bindingClass = TypeSpec.classBuilder(getBindingClassName())
                .addModifiers(Modifier.PUBLIC)
                .addField(targetIds)
                .addField(lifes)
                .addField(names)
                .addMethod(generateMethods())
                .addMethod(addKeys())
                .addMethod(addEvents())
                .addMethod(addNames())
                .addMethod(getKeys())
                .addMethod(getEvents())
                .addMethod(getNames())
                .build();
        return bindingClass;
    }

    private MethodSpec getNames() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getNames")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(spec)
                .returns(ArrayList.class)
                .addStatement("return this.names");
        return methodBuilder.build();
    }

    private MethodSpec getEvents() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getEvents")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(spec)
                .returns(ArrayList.class)
                .addStatement("return this.events");
        return methodBuilder.build();
    }

    private MethodSpec getKeys() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getKeys")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(spec)
                .returns(ArrayList.class)
                .addStatement("return this.keys");
        return methodBuilder.build();
    }

    private MethodSpec addEvents() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addEvents")
                .addAnnotation(spec)
                .addModifiers(Modifier.PRIVATE);
        for (ApiEvent event : events) {
            methodBuilder.addStatement("this.events.add($S)",event);
        }
        return methodBuilder.build();
    }

    private MethodSpec addKeys() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addKeys")
                .addAnnotation(spec)
                .addModifiers(Modifier.PRIVATE);
        for (String life : keys) {
            methodBuilder.addStatement("this.keys.add($S)",life);
        }
        return methodBuilder.build();
    }

    private MethodSpec addNames() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addNames")
                .addAnnotation(spec)
                .addModifiers(Modifier.PRIVATE);
        for (String name : names) {
            methodBuilder.addStatement("this.names.add($S)",name);
        }
        return methodBuilder.build();
    }

    @Override
    protected MethodSpec generateMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.keys = new ArrayList<>()")
                .addStatement("this.events = new ArrayList<>()")
                .addStatement("this.names = new ArrayList<>()")
                .addStatement("addEvents()")
                .addStatement("addNames()")
                .addStatement("addKeys()");
        return methodBuilder.build();
    }

    @Override
    public String getQualifiedName() {
        if(mElement instanceof ExecutableElement) {
            //被@BindView标注的应当是变量，这里简单的强制类型转换
            ExecutableElement variableElement = (ExecutableElement) mElement;
            //返回封装该元素的类
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();

            return enclosingElement.getQualifiedName().toString() + "." + variableElement.getSimpleName();
        } else if(mElement instanceof TypeElement){
            return ((TypeElement)mElement).getQualifiedName().toString();
        }
        return "unkonw";
    }

    @Override
    public String getBindingClassName() {
        if(mElement instanceof ExecutableElement) {
            //被@BindView标注的应当是变量，这里简单的强制类型转换
            ExecutableElement variableElement = (ExecutableElement) mElement;
            //返回封装该元素的类
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
            String name = enclosingElement.getQualifiedName().toString();
            String path = name.replace(".","_");
            return path+"_MethodLifecycleLoader";
        } else if(mElement instanceof TypeElement){
            String name = ((TypeElement)mElement).getQualifiedName().toString();
           String path = name.replace(".","_");
            return path+"_TypeLifecycleLoader";
        }
        return "GainLifecycleLoader";
    }
}
