package com.wxl.mvp.knife;

import android.text.TextUtils;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.http.HttpLifecycleUser;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;
import com.wxl.mvp.lifecycle.GainDialogLifecycle;
import com.wxl.mvp.lifecycle.GainFragmentLifecycle;
import com.wxl.mvp.lifecycle.GainPopLifecycle;
import com.wxl.mvp.lifecycle.Lifecycle;
import com.wxl.mvp.util.CollectionUtils;
import com.wxl.mvp.util.Loog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observable;

/**
 * create file time : 2020/12/9
 * create user : wxl
 * subscribe :
 */
public class Knife {


    /**
     * 开始寻找注解并解析一个类
     *
     * @param target
     */
    public static void findGainForKeyWordTarget(Object target) {
        if (target != null && !KnifeContainer.getInstance().isBinded(target.getClass().getName())) {
            KnifeContainer.getInstance().setRegisterObj(target);
            findTargetGainFieldAnnotation(target.getClass());
            findGainLifeTypeByTarget(target);
            findGainLifeMethodByTarget(target);
            findGainApiAnnotation(target);
            findTargetFieldSetValue(target.getClass());
            KnifeContainer.getInstance().addRelated(target.getClass());
        }
    }


    /**
     * 从target中找LifeMethod
     *
     * @param target
     */
    private static void findGainLifeMethodByTarget(Object target) {
        try {
            String name = target.getClass().getName();
            Object o = newAptClass(name, "MethodLifecycleLoader");
            if (o != null) {
                Method getKeys = o.getClass().getDeclaredMethod("getKeys");
                getKeys.setAccessible(true);
                ArrayList keys = (ArrayList) getKeys.invoke(o);

                Method getEvents = o.getClass().getDeclaredMethod("getEvents");
                getEvents.setAccessible(true);
                ArrayList events = (ArrayList) getEvents.invoke(o);

                Method getNames = o.getClass().getDeclaredMethod("getNames");
                getNames.setAccessible(true);
                ArrayList names = (ArrayList) getNames.invoke(o);

                loadGainLifeMethod(name, keys, events, names);
            }

        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (NoSuchMethodException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InvocationTargetException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
    }


    /**
     * new apt生成的类
     *
     * @param target
     * @param name
     * @return
     */
    private static Object newAptClass(String target, String name) {
        try {
            String path = target.replace(".", "_") + "_" + name;
            Class<?> aClass = Class.forName(target.substring(0, target.lastIndexOf(".")) + "." + path);
            return aClass.newInstance();
        } catch (ClassNotFoundException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InstantiationException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
        return null;
    }


    /**
     * 解析注解在方法上的生命周期
     *
     * @param keys
     * @param events
     * @param names
     */
    private static void loadGainLifeMethod(String name, ArrayList keys, ArrayList events, ArrayList names) {
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String event = (String) events.get(i);
            String name1 = (String) names.get(i);
            Target target = new Target(name1, key);
            target.setApiEvent(event);
            KnifeContainer.getInstance().putMethodLife(name, target);
        }
    }

    /**
     * 从Target中找Type的生命周期
     *
     * @param target
     */
    private static void findGainLifeTypeByTarget(Object target) {
        try {

            String name = target.getClass().getName();
            Object o = newAptClass(name, "TypeLifecycleLoader");

            if (o != null) {
                Method getKeys = o.getClass().getDeclaredMethod("getKeys");
                getKeys.setAccessible(true);
                ArrayList keys = (ArrayList) getKeys.invoke(o);

                Method getEvents = o.getClass().getDeclaredMethod("getEvents");
                getEvents.setAccessible(true);
                ArrayList events = (ArrayList) getEvents.invoke(o);

                Method getNames = o.getClass().getDeclaredMethod("getNames");
                getNames.setAccessible(true);
                ArrayList names = (ArrayList) getNames.invoke(o);

                loadGainLifeType(name, keys, events, names);
            }

        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (NoSuchMethodException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InvocationTargetException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
    }


    /**
     * 解析注解在类上的生命周期
     *
     * @param keys
     * @param events
     * @param names
     */
    private static void loadGainLifeType(String name, ArrayList keys, ArrayList events, ArrayList names) {
        Loog.methodE(name + " : " + keys.size());
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            String event = (String) events.get(i);
            String name1 = (String) names.get(i);
            Target target = new Target(name1, key);
            target.setApiEvent(event);
            KnifeContainer.getInstance().putTypeLife(name, target);
        }
    }

    /**
     * 开始寻找一个不支持没有bind的类
     *
     * @param target
     * @param lifeKey
     */
    public static void findUnSupportConstructorGainForKeyWordTarget(Object target, Class lifeKey) {
        boolean binded = findUnSupportConstructorCache(target.getClass(), lifeKey);
        if (!binded) {
            findGainForKeyWordTarget(target);
            KnifeContainer.getInstance().putUnSupportConstructorTarget(target, lifeKey);
        }
    }


    /**
     * 找到对应的Field注解
     *
     * @param cls
     */
    private static void findTargetGainFieldAnnotation(Class cls) {
        try {

            String name = cls.getName();
            Object instance = newAptClass(name, "FieldLoader");

            if (instance != null) {

                Method getTargetIds = instance.getClass().getDeclaredMethod("getTargetIds");
                getTargetIds.setAccessible(true);
                ArrayList ids = (ArrayList) getTargetIds.invoke(instance);

                Method getNames = instance.getClass().getDeclaredMethod("getNames");
                getNames.setAccessible(true);
                ArrayList names = (ArrayList) getNames.invoke(instance);

                Method getLifes = instance.getClass().getDeclaredMethod("getLifes");
                getLifes.setAccessible(true);
                ArrayList lifes = (ArrayList) getLifes.invoke(instance);

                Method getLoadChilds = instance.getClass().getDeclaredMethod("getLoadChilds");
                getLoadChilds.setAccessible(true);
                ArrayList loadChilds = (ArrayList) getLoadChilds.invoke(instance);

                parseGainFieldIdNameLife(ids, names, lifes, loadChilds);
            }

        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (NoSuchMethodException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InvocationTargetException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
    }

    /**
     * 解析Field上的注解数据
     *
     * @param ids
     * @param names
     * @param lifes
     */
    private static void parseGainFieldIdNameLife(ArrayList ids, ArrayList names, ArrayList lifes, ArrayList loadChilds) {
        for (int i = 0; i < ids.size(); i++) {
            String id = (String) ids.get(i);
            String name = (String) names.get(i);
            String lifeKey = (String) lifes.get(i);
            String key = name.substring(0, name.lastIndexOf("."));
            String field = name.substring(name.lastIndexOf(".") + 1);
            Target target = new Target(field, id);
            target.setCoverLifeKey(lifeKey);
            boolean b = Boolean.parseBoolean((String) loadChilds.get(i));
            target.setLoadChild(b);
            KnifeContainer.getInstance().putField(key, target);
        }
    }


    /**
     * 从缓存里找，是否有已经注册过该类
     *
     * @param cls
     * @return
     */
    private static boolean findUnSupportConstructorCache(Class cls, Class lifeKey) {
        Target target = KnifeContainer.getInstance().getTargetByUnSupportConstructor(cls.getName());
        if (target != null) {
            target.setCoverLifeKey(lifeKey.getName());
            findTargetFieldSetValue(cls);
            return true;
        }
        return false;
    }


    /**
     * 找到class 下的@GianField注解，并且赋值
     *
     * @param cls
     */
    private static void findTargetFieldSetValue(Class cls) {
        HashMap<String, Target> fieldTargets = KnifeContainer.getInstance().getFieldTargets(cls.getName());
        if (fieldTargets != null) {
            Object obj = KnifeContainer.getInstance().findObj(cls.getName());
            Iterator<Map.Entry<String, Target>> iterator = fieldTargets.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Target> next = iterator.next();
                Target target = next.getValue();
                Object o = findFieldTarget(cls, target);
                setFieldValue(cls, obj, target, o);
                if (target.isLoadChild() && o != null) {
                    findGainForKeyWordTarget(o);
                }
                loadLifecycleTarget(o, target.getCoverLifeKey());
            }
        }
    }


    /**
     * 为参数赋值
     *
     * @param target
     * @param o
     */
    private static void setFieldValue(Class cls, Object obj, Target target, Object o) {
        if (o != null) {
            try {
                Field field = cls.getDeclaredField(target.getName());
                field.setAccessible(true);
                if (isCanSetValue(field, o)) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isStatic) {
                        field.set(null, o);
                    } else {
                        if (obj != null) {
                            field.set(obj, o);
                        }
                    }
                }
            } catch (NoSuchFieldException e) {
                if(Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            } catch (IllegalAccessException e) {
                if(Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            }
        }
    }


    /**
     * 是否可以设置该属性
     *
     * @param field
     * @param o
     * @return
     */
    private static boolean isCanSetValue(Field field, Object o) {
        return field.getType().getName().equals(o.getClass().getName()) || isSameInterfacesClass(o.getClass(), field.getType())
                || isSameSuperClass(o.getClass(), field.getType());
    }


    /**
     * 是否是其父类型
     *
     * @param a
     * @param b
     * @return
     */
    protected static boolean isSameSuperClass(Class a, Class b) {
        Class superclass = a.getSuperclass();
        if (superclass != null) {
            return TextUtils.equals(superclass.getName(), b.getName());
        }
        return false;
    }

    /**
     * 接口类型是否相同
     *
     * @param a
     * @param type
     * @return
     */
    protected static boolean isSameInterfacesClass(Class a, Type type) {
        Type[] genericInterfaces = a.getGenericInterfaces();
        for (int i = 0; i < genericInterfaces.length; i++) {
            if (genericInterfaces[i] == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到注解对应的对象
     *
     * @param cls
     * @param target
     * @return
     */
    private static Object findFieldTarget(Class cls, Target target) {
        Object targetById = KnifeContainer.getInstance().findObj(target.getId());
        if (targetById != null) {
            return targetById;
        } else {
            return newTargetClass(cls, target);
        }
    }


    /**
     * 创建一个目标对象
     *
     * @param cls
     * @param target
     * @return
     */
    private static Object newTargetClass(Class cls, Target target) {
        Class aClass = findTargetClass(cls, target);
        if (aClass != null) {
            try {
                return aClass.newInstance();
            } catch (IllegalAccessException e) {
                if(Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            } catch (InstantiationException e) {
                if(Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            }
        }
        return null;
    }


    /**
     * 找到目标class
     *
     * @param
     * @return
     */
    private static Class findTargetClass(Class cls, Target target) {
        try {
            return Class.forName(target.getId());
        } catch (ClassNotFoundException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
        return null;
    }


    /**
     * 加载生命周期
     *
     * @param target
     * @param lifeKey
     */
    private static void loadLifecycleTarget(Object target, String lifeKey) {
        if (target instanceof Lifecycle) {
            if (!TextUtils.equals(lifeKey, GainKnife.UNSUPPORTID.getName())) {
                bindLifecycleToTarget(target, lifeKey);
            } else {
                //从target上寻找@GainLifecycle
                loadLifecycleTarget(target);
            }
        }
    }


    /**
     * 从target上寻找@GainLifecycle
     *
     * @param target
     */
    private static void loadLifecycleTarget(Object target) {
        Loog.methodE(target.getClass().getName());
        Target typeTarget = KnifeContainer.getInstance().findTypeTarget(target.getClass().getName());
        if (typeTarget != null) {
            bindLifecycleToTarget(target, typeTarget.getId());
        }
    }


    /**
     * 绑定生命周期
     *
     * @param target
     * @param lifeKey
     */
    private static void bindLifecycleToTarget(Object target, String lifeKey) {
        Object targetById = KnifeContainer.getInstance().findObj(lifeKey);
        if (targetById != null) {
            if (targetById instanceof LifecycleProvider) {
                LifecycleProvider provider = (LifecycleProvider) targetById;
                bindLifecycleToTarget(targetById.getClass(), (Lifecycle) target, provider);
            } else {
                bindLifecycleToTarget(targetById.getClass(), (Lifecycle) target, null);
            }
        }
    }


    /**
     * 绑定生命周期
     *
     * @param target
     * @param provider
     */
    private static void bindLifecycleToTarget(Class cls, Lifecycle target, LifecycleProvider provider) {
        if (provider != null) {
            Observable observable = provider.lifecycle();

            if (target instanceof GainFragmentLifecycle) {
                LifecycleObservable.get().observableFragmentEvent(cls, observable, (GainFragmentLifecycle) target);
            }

            if (target instanceof GainActivityLifecycle) {
                LifecycleObservable.get().observableActivityEvent(cls, observable, (GainActivityLifecycle) target);
            }

            if (target instanceof GainDialogLifecycle) {
                LifecycleObservable.get().observableDialogEvent(cls, observable, (GainDialogLifecycle) target);
            }

            if (target instanceof GainPopLifecycle) {
                LifecycleObservable.get().observablePopEvent(cls, observable, (GainPopLifecycle) target);
            }

        } else {
            LifecycleObservable.get().observableAttachEvent(cls, target);
        }
    }


    /**
     * 找一个类里的方法上的life注解
     *
     * @param callClass
     */
    public static LifecycleBean findHttpClassMethodLifecycleAnnotation(HttpLifecycleUser callClass) {
        Target methodLife = KnifeContainer.getInstance().getMethodLife(callClass.getUserClass().getName(), callClass.getName());
        if (methodLife != null) {
            Object obj = KnifeContainer.getInstance().findObj(methodLife.getId());
            if (obj instanceof LifecycleProvider) {
                LifecycleBean bean = new LifecycleBean((LifecycleProvider) obj, methodLife.getApiEvent());
                return bean;
            }
        }
        return findHttpClassTypeLifecycleAnnotation(callClass);
    }


    /**
     * 找一个类上的life注解
     *
     * @param callClass
     * @return
     */
    private static LifecycleBean findHttpClassTypeLifecycleAnnotation(HttpLifecycleUser callClass) {
        Target typeTarget = KnifeContainer.getInstance().findTypeTarget(callClass.getUserClass().getName());
        if (typeTarget != null) {
            Object obj = KnifeContainer.getInstance().findObj(typeTarget.getId());
            if (obj instanceof LifecycleProvider) {
                LifecycleBean bean = new LifecycleBean((LifecycleProvider) obj, typeTarget.getApiEvent());
                return bean;
            }
        }
        return null;
    }


    /**
     * 解析类下的@GainApi注解
     *
     * @param target
     */
    private static void findGainApiAnnotation(Object target) {
        try {
            String name = target.getClass().getName();
            String key = name.substring(0, name.lastIndexOf("."));
            String path = key + "." + name.replace(".", "_") + "_GainApiLoader";
            Class<?> aClass = Class.forName(path);
            Object instance = aClass.newInstance();
            Method getNames = aClass.getDeclaredMethod("getNames");
            getNames.setAccessible(true);
            ArrayList names = (ArrayList) getNames.invoke(instance);
            setTargetGainApiValue(target, names);
        } catch (ClassNotFoundException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InstantiationException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (NoSuchMethodException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InvocationTargetException e) {
            if(Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }
    }


    /**
     * 为GainApi注解赋值
     *
     * @param target
     * @param names
     */
    private static void setTargetGainApiValue(Object target, ArrayList names) {
        try {
            Class<?> aClass = target.getClass();
            for (Object name : names) {
                String field = (String) name;
                Field declaredField = aClass.getDeclaredField(field);
                declaredField.setAccessible(true);
                Object api = GainHttp.api(declaredField.getType());
                if (api != null) {
                    boolean isStatic = Modifier.isStatic(declaredField.getModifiers());
                    if (isStatic) {
                        declaredField.set(null, api);
                    } else {
                        declaredField.set(target, api);
                    }
                } else {
                    Loog.methodE("api = null ");
                }
            }
        } catch (NoSuchFieldException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 找对应的class
     * @param target
     * @param name
     * @return
     */
    private static Class findGainClass(Class target,String name){
        String path = target.getName().replace(".", "_") + "_" + name;
        try {
            return Class.forName(target.getName().substring(0, target.getName().lastIndexOf(".")) + "." + path);
        } catch (ClassNotFoundException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 释放
     * @param target
     */
    public static void releaseGainClassKnife(Object target){
        releaseGainField(target);
        releaseGainApi(target);
    }

    /**
     * 释放类上的注解@GainLifecycle
     * @param target
     */
    private static void releaseGainField(Object target){
        HashMap<String, Target> map = KnifeContainer.getInstance().getFieldTargets(target.getClass().getName());
        if(CollectionUtils.isNotEmpty(map)){
            Iterator<Map.Entry<String, Target>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Target> next = iterator.next();
                String name = next.getValue().getName();
                if(!TextUtils.isEmpty(name)){
                    try {
                        Field declaredField = target.getClass().getDeclaredField(name);
                        declaredField.setAccessible(true);
                        declaredField.set(target,null);
                        Loog.methodE("target : "+target.getClass().getName()+" field : "+name);
                    } catch (NoSuchFieldException e) {
                        if(Loog.TEST_DEBUG) {
                            e.printStackTrace();
                        }
                    } catch (IllegalAccessException e) {
                        if(Loog.TEST_DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 释放GainApi注解
     * @param target
     */
    private static void releaseGainApi(Object target){
        try {
            Class gainClass = findGainClass(target.getClass(), "GainApiLoader");
            if(gainClass != null) {
                Object instance = gainClass.newInstance();
                Method getNames = gainClass.getDeclaredMethod("getNames");
                getNames.setAccessible(true);
                ArrayList names = (ArrayList) getNames.invoke(instance);
                if (CollectionUtils.isNotEmpty(names)) {
                    for (Object name : names) {
                        Field declaredField = target.getClass().getDeclaredField((String) name);
                        declaredField.setAccessible(true);
                        declaredField.set(target,null);
                        Loog.methodE("target : "+target.getClass().getName()+" field : "+name);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (InvocationTargetException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            if(Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }

    }
}
