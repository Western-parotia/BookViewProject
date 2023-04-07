package com.juziml.read.utils.ext

import androidx.fragment.app.Fragment

/**
 * create之后执行，只执行一次
 */
fun Fragment.doOnCreated(callback: Runnable) {
    //view的viewLifecycleOwner可能没有初始化，所以先等resume再调用
    lifecycle.doOnResumed(false) {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnCreated(callback)
        }
    }
}

/**
 * 下一次resume时自行，只执行一次
 * @param ignoreBefore 监听会把之前的都发一遍，所以加此变量
 *                      false：默认效果，如果resume过会立即收到
 *                      true：等下一次resume
 */
fun Fragment.doOnResumed(
    ignoreBefore: Boolean = false,
    callback: Runnable
) {
    //view的viewLifecycleOwner可能没有初始化，所以先等resume再调用
    lifecycle.doOnResumed(false) {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnResumed(ignoreBefore, callback)
        }
    }
}

fun Fragment.doOnDestroyed(callback: Runnable) {
    lifecycle.doOnResumed(false) {
        if (view != null) {
            viewLifecycleOwner.lifecycle.doOnDestroyed(callback)
        }
    }
}