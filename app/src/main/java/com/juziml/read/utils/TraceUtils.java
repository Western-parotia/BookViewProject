package com.juziml.read.utils;

import android.util.Log;

import com.juziml.read.BuildConfig;

/**
 * create by zhusw on 2020-08-13 13:43
 */
public class TraceUtils {
    private static String msg = null;
    private static long time = 0;

    public static void startTrace(String format, Object... o) {
        if (BuildConfig.DEBUG) {
            msg = String.format(format, o);
            time = System.currentTimeMillis();
        }
    }

    public static void endTrace() {
        if (BuildConfig.DEBUG) {
            String content = String.format("TAG:%s time:%s", msg, System.currentTimeMillis() - time);
            Log.i("TraceUtils", content);
            time = 0l;
        }
    }
}
