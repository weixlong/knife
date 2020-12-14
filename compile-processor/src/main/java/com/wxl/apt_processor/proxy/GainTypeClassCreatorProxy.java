package com.wxl.apt_processor.proxy;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * create file time : 2020/10/12
 * create user : wxl
 * subscribe :
 */
public class GainTypeClassCreatorProxy extends ClassCreatorProxy {


    private  List<Integer> typeIds = new ArrayList<>();
    private  HashMap<Integer,Integer> types = new HashMap<>();
    private  HashMap<Integer,String> names = new HashMap<>();

    @Override
    public void load(Elements mElementUtils, ProcessingEnvironment processingEnv, Element element) {
        super.load(mElementUtils, processingEnv, element);
//        GainType gainType = mElement.getAnnotation(GainType.class);
//        if(gainType != null) {
//
//            int life = gainType.life();
//
//            if (typeIds.contains(id)) {
//                throw new IllegalStateException("@GainType 有重复的Id, 请确保id的唯一性，以保证可以正常使用。");
//            } else {
//                types.put(id, life);
//                names.put(id, getQualifiedName());
//                typeIds.add(id);
//            }
//        }

    }

    @Override
    public TypeSpec generateJavaCode() {

        FieldSpec view = FieldSpec.builder(HashMap.class, "types")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec names = FieldSpec.builder(HashMap.class, "names")
                .addModifiers(Modifier.PRIVATE)
                .build();

        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(view)
                .addField(names)
                .addMethod(generateMethods())
                .addMethod(generateAddTypesMethod())
                .addMethod(generateAddNamesMethod())
                .addMethod(getNamesMethod())
                .addMethod(getTypesMethod())
                .build();

        return bindingClass;
    }

    @Override
    protected MethodSpec generateMethods() {
        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.types = new HashMap<>()")
                .addStatement("this.names = new HashMap<>()")
                .addStatement("addTypes()")
                .addStatement("addNames()");
        return methodBuilder.build();
    }

    @Override
    public String getQualifiedName() {
        return ((TypeElement)mElement).getQualifiedName().toString();
    }

    @Override
    public String getBindingClassName() {
        return ((TypeElement)mElement).getSimpleName()+ "_TypeLoader";
    }

    private MethodSpec generateAddTypesMethod(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addTypes")
                .addModifiers(Modifier.PRIVATE);
        Iterator<Map.Entry<Integer, Integer>> iterator = types.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> next = iterator.next();
            methodBuilder.addStatement("this.types.put($S,$S)",next.getKey(),next.getValue());
        }
        return methodBuilder.build();
    }

    private MethodSpec generateAddNamesMethod(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("addNames")
                .addModifiers(Modifier.PRIVATE);
        Iterator<Map.Entry<Integer, String>> iterator = names.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, String> next = iterator.next();
            methodBuilder.addStatement("this.names.put($S,$S)",next.getKey(),next.getValue());
        }
        return methodBuilder.build();
    }

    private MethodSpec getNamesMethod(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getNames")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.names")
                .returns(HashMap.class);
        return methodBuilder.build();
    }

    private MethodSpec getTypesMethod(){
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getTypes")
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.types")
                .returns(HashMap.class);
        return methodBuilder.build();
    }
}
