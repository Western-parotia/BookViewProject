package com.juziml.read.utils;

import android.util.Log;

/**
 * create by zhusw on 2020-03-27 15:46
 */
public class DLog {

    public static void log(String rules, Object... args) {
        Log.i("DLog:", String.format(rules, args));
    }
}
