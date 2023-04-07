package com.juziml.read.utils.ext

import android.app.Activity
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foundation.app.application.SimpleActivityLifecycleCallbacks

/**
 * 全局回调获取activity下一个状态
 * 注意是全局，目前场景单一并且安全性较低所以改为私有，使用见[doOnMainActivityVisible]
 */
private fun Application.doOnNextActivityResumed(onActivityResumed: (activity: Activity) -> Unit) {
    registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            super.onActivityResumed(activity)
            onActivityResumed.invoke(activity)
            unregisterActivityLifecycleCallbacks(this)
        }
    })
}


fun Application.inflate(
    resId: Int,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false
): View = layoutInflater.inflate(resId, parent, attachToRoot)

val Application.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)