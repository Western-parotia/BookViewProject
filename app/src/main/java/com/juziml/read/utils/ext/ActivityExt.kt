package com.juziml.read.utils.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver
import com.juziml.read.R

/**
 * create by zhusw on 6/9/21 18:29
 */

/**
 * @param params 入参
 */
inline fun <reified T> Fragment.jumpToActivity(params: (Intent.() -> Unit) = {}) {
    val intent = Intent(activity, T::class.java)
    params(intent)
    startActivity(intent)
}

/**
 * @param params 入参
 */
inline fun <reified T> Context.jumpToActivity(params: (Intent.() -> Unit) = {}) {
    val intent = Intent(this, T::class.java)
    params(intent)
    startActivity(intent)
}

/**
 * create之后执行，只执行一次
 */
fun ComponentActivity.doOnCreated(callback: Runnable): LifecycleEventObserver? {
    return lifecycle.doOnCreated(callback)
}

/**
 * 下一次resume时自行，只执行一次
 * @param ignoreBefore 监听会把之前的都发一遍，所以加此变量
 *                      false：默认效果，如果resume过会立即收到
 *                      true：等下一次resume
 */
fun ComponentActivity.doOnResumed(
    ignoreBefore: Boolean = false,
    callback: Runnable
): LifecycleEventObserver {
    return lifecycle.doOnResumed(ignoreBefore, callback)
}

fun ComponentActivity.doOnDestroyed(callback: Runnable): LifecycleEventObserver? {
    return lifecycle.doOnDestroyed(callback)
}

/**
 * 切换状态栏颜色
 * @param isDark true：黑色文字+白色背景，false：白色文字+主色（红）背景
 */
fun Activity.switchStatusBarTheme(isDark: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decorView = this.window.decorView
        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        val flag: Int
        if (isDark) {
            this.window.statusBarColor = Color.WHITE
            flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            this.window.statusBarColor = R.color.colorPrimary.toColorInt(this)
            flag = View.SYSTEM_UI_FLAG_VISIBLE
        }
        decorView.systemUiVisibility = flag
    }
}