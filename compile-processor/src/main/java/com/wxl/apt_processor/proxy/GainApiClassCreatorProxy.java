package com.wxl.apt_processor.proxy;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/12/7
 * create user : wxl
 * subscribe :
 */
public class GainApiClassCreatorProxy extends ClassCreatorProxy {

    private List<String> names = new ArrayList<>();
    private static HashMap<String, GainApiClassCreatorProxy> cache = new HashMap<>();

    @Override
    public void load(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        if (!cache.containsKey(getBindingClassName())) {
            GainApiClassCreatorProxy proxy = new GainApiClassCreatorProxy();
            proxy.start(mElementUtils, processingEnv, element);
            cache.put(getBindingClassName(),proxy);
        } else {
            GainApiClassCreatorProxy proxy = cache.get(getBindingClassName());
            proxy.start(mElementUtils, processingEnv, element);
        }

    }

    private void start(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        names.add(classQualifiedName);
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

    public static void writeJava(ProcessingEnvironment processingEnv) {
        //processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,"cache : "+cache.size());
        Iterator<Map.Entry<String, GainApiClassCreatorProxy>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, GainApiClassCreatorProxy> next = iterator.next();
            GainApiClassCreatorProxy proxy = next.getValue();
            TypeSpec typeSpec = proxy.generateJavaCode();
           // processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, proxy.getBindingClassName());
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
        FieldSpec names = FieldSpec.builder(ArrayList.class, "names")
                .addAnnotation(spec)
                .addModifiers(Modifier.PRIVATE)
                .build();

        TypeSpec bindingClass = TypeSpec.classBuilder(getBindingClassName())
                .addModifiers(Modifier.PUBLIC)
                .addField(names)
                .addMethod(generateMethods())
                .addMethod(addNames())
                .addMethod(getNames())
                .build();
        return bindingClass;
    }

    private MethodSpec getNames() {
        AnnotationSpec spec = AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unchecked").build();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getNames")
                .addAnnotation(spec)
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList.class)
                .addStatement("return this.names");
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
                .addStatement("this.names = new ArrayList<>()")
                .addStatement("addNames()");
        return methodBuilder.build();
    }

    @Override
    public String getQualifiedName() {
        //被@BindView标注的应当是变量，这里简单的强制类型转换
        VariableElement variableElement = (VariableElement) mElement;
        //返回封装该元素的类
        TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();

        return variableElement.getSimpleName().toString();
    }

    @Override
    public String getBindingClassName() {
        VariableElement variableElement = (VariableElement) mElement;
        //返回封装该元素的类
        TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
        String replace = enclosingElement.getQualifiedName().toString().replace(".", "_");
        return replace+"_GainApiLoader";
    }
}
