package com.wxl.mvp.knife;

import android.text.TextUtils;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.wxl.gainjet.Loog;
import com.wxl.mvp.GainKnife;
import com.wxl.mvp.http.GainHttp;
import com.wxl.mvp.http.HttpLifecycleUser;
import com.wxl.mvp.lifecycle.GainActivityLifecycle;
import com.wxl.mvp.lifecycle.GainDialogLifecycle;
import com.wxl.mvp.lifecycle.GainFragmentLifecycle;
import com.wxl.mvp.lifecycle.GainPopLifecycle;
import com.wxl.mvp.lifecycle.Lifecycle;
import com.wxl.mvp.lifecycle.OnGainAttachFinishCallback;
import com.wxl.mvp.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
            findTargetSuperClass(target, target.getClass().getSuperclass(), false);
            KnifeContainer.getInstance().setRegisterObj(target);
            findTargetGainFieldAnnotation(target.getClass(), false);
            findGainLifeTypeByTarget(target.getClass(), false);
            findGainLifeMethodByTarget(target.getClass(), false);
            findGainApiAnnotation(target, target.getClass(), false);
            findTargetFieldSetValue(target.getClass(), true, false);
            KnifeContainer.getInstance().addRelated(target.getClass());
        }
    }

    /**
     * 异步加载解析
     *
     * @param target
     */
    public static void findGainForKeyWordTargetSync(Object target, boolean isAuto, ObservableEmitter<Object> e, OnGainAttachFinishCallback callback) {
        if (target != null && !KnifeContainer.getInstance().isBinded(target.getClass().getName())) {
            findTargetSuperClass(target, target.getClass().getSuperclass(), true);
            KnifeContainer.getInstance().setRegisterObj(target);
            findTargetGainFieldAnnotation(target.getClass(), true);
            findGainLifeTypeByTarget(target.getClass(), true);
            findGainLifeMethodByTarget(target.getClass(), true);
            findGainApiAnnotation(target, target.getClass(), false);
            findTargetFieldSetValue(target.getClass(), true, true);
            KnifeContainer.getInstance().addRelated(target.getClass());
            if (e != null) {
                e.onNext(target);
            }
            boolean syncTargetKeys = KnifeContainer.getInstance().isContainsSyncTargetKeys();
            if (isAuto || syncTargetKeys) {
                startSyncFindTargetChild(target, isAuto, callback);
            }
        }
    }

    /**
     * 开始异步解析被关联的对象
     */
    private static void startSyncFindTargetChild(Object target, boolean isAuto, OnGainAttachFinishCallback callback) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                List<String> syncTargetKeys = KnifeContainer.getInstance().getSyncTargetKeys();
                if (CollectionUtils.isNotEmpty(syncTargetKeys)) {
                    for (int i = syncTargetKeys.size() - 1; i >= 0; i--) {
                        List<Object> objects = KnifeContainer.getInstance().getSyncTarget(syncTargetKeys.get(i));
                        if (CollectionUtils.isNotEmpty(objects)) {
                            for (int i1 = objects.size() - 1; i1 >= 0; i1--) {
                                findGainForKeyWordTargetSync(objects.get(i1), false, e, callback);
                            }
                            objects.clear();
                        }
                    }
                }
                e.onNext(syncTargetKeys);
                if (isAuto) {
                    e.onNext(target);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        synchronized (Knife.class) {
                            if (o instanceof List) {
                                List list = (List) o;
                                list.clear();
                            } else if (callback != null) {
                                callback.onSyncAttachFinish(o);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 从target中找LifeMethod
     *
     * @param target
     */
    private static void findGainLifeMethodByTarget(Class target, boolean isSync) {
        String name = target.getName();
        newAptClass(name, "GainMLL", new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                try {
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
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (NoSuchMethodException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (InvocationTargetException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                }
            }
        }, isSync);

    }


    /**
     * new apt生成的类
     *
     * @param target
     * @param name
     * @return
     */
    private static void newAptClass(String target, String name, Consumer<Object> consumer, boolean isSync) {
        try {
            String[] path = target.split("\\.");
            String p = path[path.length - 1] + name;
            Class<?> aClass = Class.forName(target.substring(0, target.lastIndexOf(".")) + "." + p);
            if (consumer != null) {
                MainTarget.getInstance().takeNewObjectMain(consumer, aClass, false);
            }
        } catch (ClassNotFoundException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (Exception e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        }

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
    private static void findGainLifeTypeByTarget(Class target, boolean isSync) {
        String name = target.getName();
        newAptClass(name, "GainTLL", new Consumer<Object>() {

            @Override
            public void accept(Object o) throws Exception {
                try {
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
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (NoSuchMethodException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (InvocationTargetException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                }
            }
        }, isSync);


    }


    /**
     * 解析注解在类上的生命周期
     *
     * @param keys
     * @param events
     * @param names
     */
    private static void loadGainLifeType(String name, ArrayList keys, ArrayList events, ArrayList names) {
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
     * 找目标对象的父类，如果存在父类并且父类中包含可解析字段
     * 其他如：GainLifecycle 注解即在该父类调用时自动解析生命周期
     * 故：只解析对应的字段
     *
     * @param target
     */
    private static void findTargetSuperClass(Object target, Class<?> superclass, boolean isSync) {
        if (superclass != null) {
            findTargetGainFieldAnnotation(superclass, isSync);
            findGainLifeTypeByTarget(superclass, isSync);
            findGainLifeMethodByTarget(superclass, isSync);
            findGainApiAnnotation(target, superclass, true);
            findTargetSuperFieldSetValue(superclass, target, true, isSync);
            KnifeContainer.getInstance().addRelated(superclass);
            Class<?> superclassSuperclass = superclass.getSuperclass();
            if (superclassSuperclass != null) {
                findTargetSuperClass(target, superclassSuperclass, isSync);
            }
        }
    }


    /**
     * 找到class 下的@GianField注解，并且赋值
     *
     * @param cls
     */
    private static void findTargetSuperFieldSetValue(Class cls, Object childObj, boolean isLoadAttach, boolean isSync) {
        HashMap<String, Target> fieldTargets = KnifeContainer.getInstance().getFieldTargets(cls.getName());
        if (fieldTargets != null) {
            Object obj = KnifeContainer.getInstance().findObj(cls.getName());
            Object[] keySet = fieldTargets.keySet().toArray();
            for (int i = keySet.length - 1; i >= 0; i--) {
                Target target = fieldTargets.get((String) keySet[i]);
                Consumer<Object> superConsumer = MainTarget.getInstance().getSuperConsumer();
                if(superConsumer == null){
                    superConsumer = new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            setFieldValue(cls, obj == null ? childObj : obj, target, o);
                            if (target.isLoadChild() && o != null) {
                                if (isSync) {
                                    KnifeContainer.getInstance().addSyncTarget(o);
                                } else {
                                    findGainForKeyWordTarget(o);
                                }
                            }
                            loadLifecycleTarget(o, target.getCoverLifeKey(), isLoadAttach, isSync);
                        }
                    };
                }
                findFieldTarget(cls, target, isSync, superConsumer);
            }
        }
    }

    /**
     * 开始寻找一个不支持Constructor没有bind的类
     *
     * @param target
     * @param lifeKey
     */
    public static void findUnSupportConstructorGainForKeyWordTarget(Object target, Class lifeKey, boolean isSync) {
        boolean binded = findUnSupportConstructorCache(target.getClass(), lifeKey, isSync);
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
    private static void findTargetGainFieldAnnotation(Class cls, boolean isSync) {
        String name = cls.getName();
        newAptClass(name, "GainFL", new Consumer<Object>() {

            @Override
            public void accept(Object instance) throws Exception {
                try {
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
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (NoSuchMethodException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                } catch (InvocationTargetException e) {
                    if (Loog.TEST_DEBUG) {
                        Loog.expection(e);
                    }
                }
            }
        }, isSync);
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
    private static boolean findUnSupportConstructorCache(Class cls, Class lifeKey, boolean isSync) {
        Target target = KnifeContainer.getInstance().getTargetByUnSupportConstructor(cls.getName());
        if (target != null) {
            target.setCoverLifeKey(lifeKey.getName());
            findTargetFieldSetValue(cls, true, isSync);
            return true;
        }
        return false;
    }


    /**
     * 找到class 下的@GianField注解，并且赋值
     *
     * @param cls
     */
    private static void findTargetFieldSetValue(Class cls, boolean isLoadAttach, boolean isSync) {
        HashMap<String, Target> fieldTargets = KnifeContainer.getInstance().getFieldTargets(cls.getName());
        if (fieldTargets != null) {
            Object obj = KnifeContainer.getInstance().findObj(cls.getName());
            Object[] keySet = fieldTargets.keySet().toArray();
            for (int length = 0; length < keySet.length; length++) {
                Target target = fieldTargets.get((String) keySet[length]);
                Consumer<Object> consumer = MainTarget.getInstance().getConsumer();
                if(consumer == null) {
                    consumer = new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            setFieldValue(cls, obj, target, o);
                            if (target.isLoadChild() && o != null) {
                                if (isSync) {
                                    KnifeContainer.getInstance().addSyncTarget(o);
                                } else {
                                    findGainForKeyWordTarget(o);
                                }
                            }
                            loadLifecycleTarget(o, target.getCoverLifeKey(), isLoadAttach, isSync);
                        }
                    };
                }
                findFieldTarget(cls, target, isSync, consumer);
            }
        }
    }


    /**
     * 唤醒某个类的生命周期，如果对同一个类进行了不同的生命周期指定，在回调之前的类时需要唤醒该生命周期
     *
     * @param cls 恢复到该class 上
     */
    public static void onResumeTargetLifecycle(Class cls) {
        KnifeContainer.getInstance().setMainClass(cls);
        findTargetFieldSetValue(cls, false, false);
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
                if (Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            } catch (IllegalAccessException e) {
                if (Loog.TEST_DEBUG) {
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
    private static void findFieldTarget(Class cls, Target target, boolean isSync, Consumer<Object> consumer) {
        Object targetById = KnifeContainer.getInstance().findObj(target.getId());
        if (targetById != null && consumer != null) {
            try {
                consumer.accept(targetById);
            } catch (Exception e) {
                if (Loog.TEST_DEBUG) {
                    Loog.expection(e);
                }
            }
        } else {
            newTargetClass(cls, target, isSync, consumer);
        }
    }


    /**
     * 创建一个目标对象
     *
     * @param cls
     * @param target
     * @return
     */
    private static void newTargetClass(Class cls, Target target, boolean isSync, Consumer<Object> consumer) {
        Class aClass = findTargetClass(cls, target);
        if (aClass != null) {
            MainTarget.getInstance().takeNewObjectMain(consumer,aClass,false);
        }
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
            if (Loog.TEST_DEBUG) {
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
    private static void loadLifecycleTarget(Object target, String lifeKey, boolean isLoadAttach, boolean isSync) {
        if (target instanceof Lifecycle) {
            if (!TextUtils.equals(lifeKey, GainKnife.UNSUPPORTID.getName())) {
                bindLifecycleToTarget(target, lifeKey, isLoadAttach, isSync);
            } else {
                //从target上寻找@GainLifecycle
                loadLifecycleTarget(target, isLoadAttach, isSync);
            }
        }
    }


    /**
     * 从target上寻找@GainLifecycle
     *
     * @param target
     */
    private static void loadLifecycleTarget(Object target, boolean isLoadAttach, boolean isSync) {
        Target typeTarget = KnifeContainer.getInstance().findTypeTarget(target.getClass().getName());
        if (typeTarget != null) {
            bindLifecycleToTarget(target, typeTarget.getId(), isLoadAttach, isSync);
        }
    }


    /**
     * 绑定生命周期
     *
     * @param target
     * @param lifeKey
     */
    private static void bindLifecycleToTarget(Object target, String lifeKey, boolean isLoadAttach, boolean isSync) {
        Object targetById = KnifeContainer.getInstance().findObj(lifeKey);
        if (targetById != null) {
            if (targetById instanceof LifecycleProvider) {
                LifecycleProvider provider = (LifecycleProvider) targetById;
                bindLifecycleToTarget(targetById.getClass(), (Lifecycle) target, provider, isLoadAttach, isSync);
            } else {
                bindLifecycleToTarget(targetById.getClass(), (Lifecycle) target, null, isLoadAttach, isSync);
            }
        }
    }


    /**
     * 绑定生命周期
     *
     * @param target
     * @param provider
     */
    private static void bindLifecycleToTarget(Class cls, Lifecycle target, LifecycleProvider provider, boolean isLoadAttach, boolean isSync) {
        if (provider != null) {
            Observable observable = provider.lifecycle();
            if (target instanceof GainFragmentLifecycle) {
                LifecycleObservable.get().observableFragmentEvent(cls, observable, (GainFragmentLifecycle) target, isLoadAttach, isSync);
            } else if (target instanceof GainActivityLifecycle) {
                LifecycleObservable.get().observableActivityEvent(cls, observable, (GainActivityLifecycle) target, isLoadAttach, isSync);
            } else if (target instanceof GainDialogLifecycle) {
                LifecycleObservable.get().observableDialogEvent(cls, observable, (GainDialogLifecycle) target, isSync);
            } else if (target instanceof GainPopLifecycle) {
                LifecycleObservable.get().observablePopEvent(cls, observable, (GainPopLifecycle) target, isSync);
            } else {
                LifecycleObservable.get().observableAttachEvent(cls, target, isSync);
            }
        } else {
            LifecycleObservable.get().observableAttachEvent(cls, target, isSync);
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
    private static void findGainApiAnnotation(Object target, Class targetCls, boolean isSuperClass) {
        try {
            String name = targetCls.getName();
            String key = name.substring(0, name.lastIndexOf("."));
            String[] split = name.split("\\.");
            String path = key + "." + split[split.length - 1] + "GainAL";
            Class<?> aClass = Class.forName(path);

            Object instance = aClass.newInstance();
            Method getNames = aClass.getDeclaredMethod("getNames");
            getNames.setAccessible(true);
            ArrayList names = (ArrayList) getNames.invoke(instance);
            setTargetGainApiValue(target, targetCls, names, isSuperClass);
        } catch (ClassNotFoundException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (IllegalAccessException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InstantiationException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (NoSuchMethodException e) {
            if (Loog.TEST_DEBUG) {
                Loog.expection(e);
            }
        } catch (InvocationTargetException e) {
            if (Loog.TEST_DEBUG) {
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
    private static void setTargetGainApiValue(Object target, Class targetCls, ArrayList names, boolean isSuperClass) {
        try {
            Class<?> aClass = target.getClass();
            for (Object name : names) {
                String field = (String) name;
                Field declaredField = isSuperClass ? targetCls.getDeclaredField(field) : aClass.getDeclaredField(field);
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
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 找对应的class
     *
     * @param target
     * @param name
     * @return
     */
    private static Class findGainClass(Class target, String name) {
        try {
            String[] split = target.getName().split("\\.");
            String path = split[split.length - 1] + name;
            return Class.forName(target.getName().substring(0, target.getName().lastIndexOf(".")) + "." + path);
        } catch (ClassNotFoundException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 释放
     *
     * @param target
     */
    public static void releaseGainClassKnife(Object target) {
        GainAttachArgs.getInstance().clearAttachArgs(target.getClass());
        releaseGainField(target);
        releaseGainApi(target);
    }

    /**
     * 释放类上的注解@GainLifecycle
     *
     * @param target
     */
    private static void releaseGainField(Object target) {
        HashMap<String, Target> map = KnifeContainer.getInstance().getFieldTargets(target.getClass().getName());
        if (CollectionUtils.isNotEmpty(map)) {
            Iterator<Map.Entry<String, Target>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Target> next = iterator.next();
                String name = next.getValue().getName();
                if (!TextUtils.isEmpty(name)) {
                    try {
                        Field declaredField = target.getClass().getDeclaredField(name);
                        declaredField.setAccessible(true);
                        declaredField.set(target, null);
                    } catch (NoSuchFieldException e) {
                        if (Loog.TEST_DEBUG) {
                            e.printStackTrace();
                        }
                    } catch (IllegalAccessException e) {
                        if (Loog.TEST_DEBUG) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 释放GainApi注解
     *
     * @param target
     */
    private static void releaseGainApi(Object target) {
        try {
            Class gainClass = findGainClass(target.getClass(), "GAL");
            if (gainClass != null) {
                Object instance = gainClass.newInstance();
                Method getNames = gainClass.getDeclaredMethod("getNames");
                getNames.setAccessible(true);
                ArrayList names = (ArrayList) getNames.invoke(instance);
                if (CollectionUtils.isNotEmpty(names)) {
                    for (Object name : names) {
                        Field declaredField = target.getClass().getDeclaredField((String) name);
                        declaredField.setAccessible(true);
                        declaredField.set(target, null);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (InvocationTargetException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            if (Loog.TEST_DEBUG) {
                e.printStackTrace();
            }
        }

    }


}
