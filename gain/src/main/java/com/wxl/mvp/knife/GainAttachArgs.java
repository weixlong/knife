package com.wxl.mvp.knife;

import java.util.HashMap;

/**
 * create file time : 2021/1/22
 * create user : wxl
 * subscribe : 携带参数，要在调用绑定前进行设置才能生效
 */
public class GainAttachArgs {

    private static class GAA {
        private static GainAttachArgs gaa = new GainAttachArgs();
        private static HashMap<String,Object[]> args = new HashMap<>();
    }

    private GainAttachArgs() {
    }

    /**
     * 单例
     * @return
     */
    public static GainAttachArgs getInstance() {
        return GAA.gaa;
    }


    /**
     * 设置绑定时的调用参数，必须在绑定前进行设置，否则不会生效，将会在unbind时同步释放
     * @param targetCls 实现Lifecycle接口的实体类
     * @param args 参数
     * @return
     */
    public GainAttachArgs setArgs(Class targetCls,Object... args){
        if(targetCls != null && args != null && args.length > 0){
            GAA.args.put(targetCls.getName(),args);
        }
        return this;
    }


    /**
     * 获得绑定参数
     * @param targetCls
     * @return
     */
    protected Object[] getAttachArgs(Class targetCls){
        return GAA.args.get(targetCls.getName());
    }


    /**
     * 清除参数
     * @param targetCls
     */
    protected void clearAttachArgs(Class targetCls){
        if(GAA.args.containsKey(targetCls.getName())){
           GAA.args.remove(targetCls.getName());
        }
    }
}
