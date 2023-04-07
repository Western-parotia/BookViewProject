package com.juziml.read.utils.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.juziml.read.core.ArchConfig

/**
 * 颜色相关
 * create by zhusw on 8/3/21 21:47
 */
@ColorInt
fun Activity.findColor(@ColorRes id: Int): Int {
    return id.toColorInt
}

@ColorInt
fun Fragment.findColor(@ColorRes id: Int): Int {
    return id.toColorInt
}

@ColorInt
fun View.findColor(@ColorRes id: Int): Int {
    //带context预览视图才能正常
    return id.toColorInt(context)
}

@ColorInt
fun Dialog.findColor(@ColorRes id: Int): Int {
    return id.toColorInt
}

val Int.toColorInt
    @ColorInt
    get() = this.toColorInt()

@ColorInt
@JvmOverloads
fun Int.toColorInt(context: Context = ArchConfig.app) = ContextCompat.getColor(context, this)
