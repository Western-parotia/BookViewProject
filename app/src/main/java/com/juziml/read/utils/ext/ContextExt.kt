package com.juziml.read.utils.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.juziml.read.core.ArchConfig

fun Context.safetyStartActivity(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        if (ArchConfig.debug) {
            throw e
        }
    }
}

fun Context.notSafetyStartActivity(intent: Intent) {
    startActivity(intent)
}


fun Activity.safetyStartActivityForResult(intent: Intent, requestCode: Int) {
    try {
        startActivityForResult(intent, requestCode)
    } catch (e: Exception) {
        e.printStackTrace()
        if (ArchConfig.debug) {
            throw e
        }
    }
}

