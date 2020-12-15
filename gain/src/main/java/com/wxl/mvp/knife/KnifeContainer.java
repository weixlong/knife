package com.wxl.mvp.knife;

import android.text.TextUtils;

import com.wxl.mvp.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe : 用于存放当前正在使用的类
 */
public class KnifeContainer {

    private static HashMap<String, HashMap<String, HashMap<String, Target>>> fieldContainer = new HashMap<>();
    private static HashMap<String, Target> unSupportConstructorContainer = new HashMap<>();
    private static HashMap<String, Object> registerObjectContainer = new HashMap<>();
    private static HashMap<String, List<Class>> registerRelatedPool = new HashMap<>();
    private  Class mainClass = null;


    private static class KC {
        private static KnifeContainer container = new KnifeContainer();
    }

    private KnifeContainer() {
    }

    public static KnifeContainer getInstance() {
        return KC.container;
    }

    /**
     * 设置当前绑定的class
     * @param mainClass
     */
    public void setMainClass(Class mainClass){
        this.mainClass = mainClass;
    }

    /**
     * 添加关联class
     * @param
     * @param related
     */
    public void addRelated(Class related){
        if(mainClass == null) return;
        if(!registerRelatedPool.containsKey(mainClass.getName())){
            registerRelatedPool.put(mainClass.getName(),new ArrayList<>());
        }
        List<Class> list = registerRelatedPool.get(mainClass.getName());
        if(!list.contains(related)) {
            list.add(related);
        }
    }


    /**
     * 存注解在类上的生命周期注解
     *
     * @param name   类全名
     * @param target
     */
    public void putTypeLife(String name, Target target) {
        HashMap<String, Target> targets = getContainer(name, "TypeLifecycleLoader");
        if (CollectionUtils.isNull(targets)) {
            targets = new HashMap<>();
        }
        targets.put(name, target);
    }


    /**
     * 找注解在类上的生命周期
     *
     * @param name
     * @return
     */
    public Target findTypeTarget(String name) {
        HashMap<String, Target> typeLifecycleLoader = getContainer(name, "TypeLifecycleLoader");
        if (CollectionUtils.isNotEmpty(typeLifecycleLoader)) {
            return typeLifecycleLoader.get(name);
        }
        return null;
    }

    /**
     * 存注解在方法上的生命周期注解
     *
     * @param name 类全名
     */
    public void putMethodLife(String name, Target target) {
        HashMap<String, Target> targets = getContainer(name, "MethodLifecycleLoader");
        if (CollectionUtils.isNull(targets)) {
            targets = new HashMap<>();
        }
        String[] split = target.getName().split("\\.");
        targets.put(split[split.length-1], target);
    }

    /**
     * 找注解在方法上的生命周期
     *
     * @param name
     * @param method
     */
    public Target getMethodLife(String name, String method) {
        HashMap<String, Target> methodLifecycleLoader = getContainer(name, "MethodLifecycleLoader");
        if (CollectionUtils.isNotEmpty(methodLifecycleLoader)) {
            return methodLifecycleLoader.get(method);
        }
        return null;
    }


    /**
     * 存注解属性
     *
     * @param name 类全名
     */
    public void putField(String name, Target target) {
        HashMap<String, Target> targets = getContainer(name, "FieldLoader");
        if (CollectionUtils.isNull(targets)) {
            targets = new HashMap<>();
        }
        targets.put(target.getName(), target);
    }


    /**
     * 生成对应的key值
     *
     * @param name
     * @param tag
     * @return
     */
    private String getNameKey(String name, String tag) {
        return name.replace(".", "_") + "_" + tag;
    }

    /**
     * 获得对应的容器
     *
     * @param name
     * @param tag
     * @return
     */
    private HashMap<String, Target> getContainer(String name, String tag) {
        String path = getNameKey(name, tag);
        loadContainerNotNull(name, path);
        HashMap<String, HashMap<String, Target>> hashMap = fieldContainer.get(name);
        return hashMap.get(path);
    }

    /**
     * 保证容器不为空
     *
     * @param name
     * @param path
     */
    private void loadContainerNotNull(String name, String path) {
        if (!fieldContainer.containsKey(name)) {
            fieldContainer.put(name, new HashMap<>());
        }
        HashMap<String, HashMap<String, Target>> hashMap = fieldContainer.get(name);
        if (!hashMap.containsKey(path)) {
            hashMap.put(path, new HashMap<>());
        }
    }


    /**
     * 找一个类下的所有Field注解
     *
     * @param name
     * @return
     */
    public HashMap<String, Target> getFieldTargets(String name) {
        return getContainer(name, "FieldLoader");
    }


    /**
     * 找方法上的Lifecycle注解
     * @param name
     * @return
     */
    public HashMap<String,Target> getMethodLifecycleLoader(String name){
        return getContainer(name,"MethodLifecycleLoader");
    }

    /**
     * 找类上的Lifecycle注解
     * @param name
     * @return
     */
    public HashMap<String,Target> getTypeLifecycleLoader(String name){
        return  getContainer(name, "TypeLifecycleLoader");
    }

    /**
     * 从field里面找具有target.obj的对象
     *
     * @param name
     * @return
     */
    public Target getFieldTarget(String name, String fieldName) {
        HashMap<String, Target> targets = getContainer(name, "FieldLifecycleLoader");
        if (CollectionUtils.isNotEmpty(targets)) {
            return targets.get(fieldName);
        }
        return null;
    }


    /**
     * 找一个具有target.obj的对象
     *
     * @param name
     * @return
     */
    public Target getTarget(String name, String fieldName) {
        Target fieldTarget = getFieldTarget(name, fieldName);
        if (fieldTarget == null) {
            return getTargetByUnSupportConstructor(name);
        }
        return fieldTarget;
    }

    /**
     * 从register里面找具有target.obj的对象
     *
     * @param name
     * @return
     */
    public Target getTargetByUnSupportConstructor(String name) {
        return unSupportConstructorContainer.get(name);
    }


    /**
     * 找一个被注册的对象
     *
     * @param name
     * @return
     */
    public Object findObj(String name) {
        return registerObjectContainer.get(name);
    }


    /**
     * 注册一个对象
     *
     * @param o
     */
    public void setRegisterObj(Object o) {
        registerObjectContainer.put(o.getClass().getName(), o);
    }


    /**
     * 是否已被注册
     *
     * @param name
     * @return
     */
    public boolean isBinded(String name) {
        return registerObjectContainer.containsKey(name);
    }


    /**
     * 移除注册
     *
     * @param name
     */
    public void remove(String name) {
        removeUnSupport(name);
        boolean re = registerObjectContainer.containsKey(name);
        if (re) {
            Object remove = registerObjectContainer.remove(name);
            if(remove != null) {
                Knife.releaseGainClassKnife(remove);
            }
        }
        boolean containsKey = fieldContainer.containsKey(name);
        if (containsKey) {
            fieldContainer.remove(name);
        }
    }


    /**
     * 移除改类下的所有关联
     * @param cls
     */
    public void unRelated(String cls){
        if(TextUtils.equals(cls,mainClass.getName())){
            mainClass = null;
        }
        boolean b = registerRelatedPool.containsKey(cls);
        if(b){
            remove(cls);
            List<Class> list = registerRelatedPool.get(cls);
            if(CollectionUtils.isNotEmpty(list)){
                for (Class aClass : list) {
                    remove(aClass.getName());
                }
            }
        }
    }

    /**
     * 移除一个不可以被无参构造初始化的对象
     *
     * @param name
     */
    public void removeUnSupport(String name) {
        boolean b = unSupportConstructorContainer.containsKey(name);
        if (b) {
            Target remove = unSupportConstructorContainer.remove(name);
            if(remove != null && remove.getTarget() != null){
                Knife.releaseGainClassKnife(remove.getTarget());
            }
        }
    }


    /**
     * 注册一个不可以被无参构造初始化的对象
     *
     * @param target
     * @param lifeKey
     */
    public void putUnSupportConstructorTarget(Object target, Class lifeKey) {
        Target target1 = new Target(target.getClass().getName(), lifeKey.getName());
        target1.setCoverLifeKey(lifeKey.getName());
        registerObjectContainer.put(target.getClass().getName(), target);
        unSupportConstructorContainer.put(target.getClass().getName(), target1);
    }
}
