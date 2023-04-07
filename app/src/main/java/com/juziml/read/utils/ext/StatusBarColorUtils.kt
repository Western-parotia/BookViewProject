package com.juziml.read.utils.ext

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

/**
 * 修改状态栏颜色（浅色或暗色）
 * 如果状态栏设置为了半透明 则只能是白色
 * create by zhusw on 6/8/21 19:29
 */
object StatusBarColorUtils {

    /**
     * [switchStatusBarTheme]
     */
    @Deprecated("有时间再合并到StatusBar里吧")
    @JvmStatic
    fun switchStatusBarTextColor(activity: Activity?, isDark: Boolean) {
        if (null == activity) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = Color.WHITE
            val decorView = activity.window.decorView
            val flag =
                if (isDark) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else View.SYSTEM_UI_FLAG_VISIBLE
            decorView.systemUiVisibility = flag
        }
    }
}