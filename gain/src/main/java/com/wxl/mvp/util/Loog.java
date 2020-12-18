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

    public static final boolean TEST_DEBUG = false;

    public static void d(String msg) {
        Log.d(TAG, msg);
    }


    public static void e(String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - TAG.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.e(TAG, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.e(TAG, msg);
        }
    }

    public static void methodE(String msg) {
        if (GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            String title = className + "." + methodName + "() : ";
            int max_str_length = 2001 - TAG.length() - title.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.e(TAG, title + msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            Log.e(TAG, title+ msg);
        }
    }

    public static void methodI(String msg) {
        if (GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            String title = className + "." + methodName + "() : ";
            int max_str_length = 2001 - TAG.length() - title.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.i(TAG, title + msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            Log.i(TAG, title+ msg);
        }
    }


    public static void methodV(String msg) {
        if (GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            String title = className + "." + methodName + "() : ";
            int max_str_length = 2001 - TAG.length() - title.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.v(TAG, title + msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            Log.v(TAG, title+ msg);
        }
    }

    public static void methodD(String msg) {
        if (GainNote.isDebug()) {
            StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
            String className = traceElement.getClassName();
            String methodName = traceElement.getMethodName();
            String title = className + "." + methodName + "() : ";
            int max_str_length = 2001 - TAG.length() - title.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.d(TAG, title + msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            Log.d(TAG, title+ msg);
        }
    }


    public static void expection(Exception e) {
        if (GainNote.isDebug()) {
            e.printStackTrace();
        }
    }

    public static void i(String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - TAG.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.i(TAG, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.i(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - TAG.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.v(TAG, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.v(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.d(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.i(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.e(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (GainNote.isDebug()) {
            int max_str_length = 2001 - tag.length();
            //大于4000时
            while (msg.length() > max_str_length) {
                Log.v(tag, msg.substring(0, max_str_length));
                msg = msg.substring(max_str_length);
            }
            //剩余部分
            Log.v(tag, msg);
        }
    }
}
