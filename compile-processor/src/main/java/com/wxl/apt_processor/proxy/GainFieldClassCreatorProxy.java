package com.wxl.apt_processor.proxy;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wxl.apt_annotation.GainField;

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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public class GainFieldClassCreatorProxy extends ClassCreatorProxy {

    private List<String> ids = new ArrayList<>();
    private List<String> lifes = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private List<Boolean> loadChilds = new ArrayList<>();
    private static HashMap<String, GainFieldClassCreatorProxy> cache = new HashMap<>();

    @Override
    public void load(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        if (!cache.containsKey(getBindingClassName())) {
            GainFieldClassCreatorProxy proxy = new GainFieldClassCreatorProxy();
            proxy.start(mElementUtils, processingEnv, element);
            cache.put(getBindingClassName(), proxy);
        } else {
            GainFieldClassCreatorProxy proxy = cache.get(getBindingClassName());
            proxy.start(mElementUtils, processingEnv, element);
        }
    }

    public void start(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        init(mElementUtils, processingEnv, element);
        GainField gainField = mElement.getAnnotation(GainField.class);
        if (gainField != null) {
            try {
                gainField.target();
            } catch (MirroredTypeException e) {
               // processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "---->" + e.getTypeMirror().toString());
                ids.add(e.getTypeMirror().toString());
                try {
                    gainField.life();
                } catch (MirroredTypeException e1) {
                    lifes.add(e1.getTypeMirror().toString());
                }

                names.add(getQualifiedName());
                loadChilds.add(gainField.isLoadChild());
            }
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

    public static void writeJava(ProcessingEnvironment processingEnv) {
        //processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "cache : " + cache.size());
        Iterator<Map.Entry<String, GainFieldClassCreatorProxy>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, GainFieldClassCreatorProxy> next = iterator.next();
            GainFieldClassCreatorProxy proxy = next.getValue();
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

        FieldSpec targetIds = FieldSpec.builder(ArrayList.class, "targetIds")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec lifes = FieldSpec.builder(ArrayList.class, "lifes")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec names = FieldSpec.builder(ArrayList.class, "names")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec loadChilds = FieldSpec.builder(ArrayList.class, "loadChilds")
                .addModifiers(Modifier.PRIVATE)
                .build();

        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(targetIds)
                .addField(lifes)
                .addField(names)
                .addField(loadChilds)
                .addMethod(generateMethods())
                .addMethod(addLoadChilds())
                .addMethod(addTargetIds())
                .addMethod(addLifes())
                .addMethod(addNames())
                .addMethod(getTargetIds())
                .addMethod(getLifes())
                .addMethod(getNames())
                .addMethod(getLoadChilds())
                .build();
        return bindingClass;
    }

    @Override
    protected MethodSpec generateMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.targetIds = new ArrayList<>()")
                .addStatement("this.lifes = new ArrayList<>()")
                .addStatement("this.names = new ArrayList<>()")
                .addStatement("this.loadChilds = new ArrayList<>()")
                .addStatement("addLoadChilds()")
                .addStatement("addTargetIds()")
                .addStatement("addNames()")
                .addStatement("addLifes()");
        return methodBuilder.build();
    }

    @Override
    public String getQualifiedName() {
        //被@BindView标注的应当是变量，这里简单的强制类型转换
        VariableElement variableElement = (VariableElement) mElement;
        //返回封装该元素的类
        TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();

        return enclosingElement.getQualifiedName().toString() + "." + variableElement.getSimpleName();
    }

    @Override
    public String getBindingClassName() {
        //被@BindView标注的应当是变量，这里简单的强制类型转换
        VariableElement variableElement = (VariableElement) mElement;
        //返回封装该元素的类
        if (variableElement != null) {
            TypeElement enclosingElement = (TypeElement) variableElement.getEnclosingElement();
            String name = enclosingElement.getQualifiedName().toString();
            String path = name.replace(".", "_");
            return path + "_FieldLoader";
        }
        return "";
    }

    private MethodSpec addLoadChilds() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addLoadChilds")
                .addModifiers(Modifier.PRIVATE);
        for (Boolean b : loadChilds) {
            methodBuilder.addStatement("this.loadChilds.add($S)", b);
        }
        return methodBuilder.build();
    }

    private MethodSpec addTargetIds() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addTargetIds")
                .addModifiers(Modifier.PRIVATE);
        for (String id : ids) {
            methodBuilder.addStatement("this.targetIds.add($S)", id);
        }
        return methodBuilder.build();
    }

    private MethodSpec addLifes() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addLifes")
                .addModifiers(Modifier.PRIVATE);
        for (String life : lifes) {
            methodBuilder.addStatement("this.lifes.add($S)", life);
        }
        return methodBuilder.build();
    }

    private MethodSpec addNames() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addNames")
                .addModifiers(Modifier.PRIVATE);
        for (String name : names) {
            methodBuilder.addStatement("this.names.add($S)", name);
        }
        return methodBuilder.build();
    }


    private MethodSpec getLoadChilds() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getLoadChilds")
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList.class)
                .addStatement("return this.loadChilds");
        return methodBuilder.build();
    }


    private MethodSpec getTargetIds() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getTargetIds")
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList.class)
                .addStatement("return this.targetIds");
        return methodBuilder.build();
    }

    private MethodSpec getLifes() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getLifes")
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList.class)
                .addStatement("return this.lifes");
        return methodBuilder.build();
    }


    private MethodSpec getNames() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getNames")
                .addModifiers(Modifier.PUBLIC)
                .returns(ArrayList.class)
                .addStatement("return this.names");
        return methodBuilder.build();
    }
}
