package com.wxl.mvp.util;

import android.util.Log;

import com.wxl.mvp.GainNote;

/**
 * create file time : 2020/12/11
 * create user : wxl
 * subscribe :
 */
public class Loog {

    private static final String TAG = Loog.class.getSimpleName();

    public static void d(String msg){
        Log.d(TAG,msg);
    }


    public static void e(String msg){
        if(GainNote.isDebug()) {
            Log.e(TAG, msg);
        }
    }

    public static void methodE(String msg){
        if(GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            Log.e(TAG, className+"."+methodName + "() : " + msg);
        }
    }

    public static void methodI(String msg){
        if(GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            Log.i(TAG, className+"."+methodName + "() : " + msg);
        }
    }


    public static void methodV(String msg){
        if(GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            Log.v(TAG, className+"."+methodName + "() : " + msg);
        }
    }

    public static void methodD(String msg){
        if(GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            Log.d(TAG, className+"."+methodName + "() : " + msg);
        }
    }


    public static void expection(Exception e){
        if(GainNote.isDebug()) {
            e.printStackTrace();
        }
    }

    public static void i(String msg){
        if(GainNote.isDebug()) {
            Log.i(TAG, msg);
        }
    }

    public static void v(String msg){
        if(GainNote.isDebug()) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String tag,String msg){
        if(GainNote.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag,String msg){
        if(GainNote.isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag,String msg){
        if(GainNote.isDebug()) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag,String msg){
        if(GainNote.isDebug()) {
            Log.v(tag, msg);
        }
    }
}
