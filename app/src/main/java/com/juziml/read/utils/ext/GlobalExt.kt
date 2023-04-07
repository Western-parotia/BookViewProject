package com.juziml.read.utils.ext

import android.content.res.Resources

/**
 * create by zhusw on 6/9/21 21:08
 */

/**
 *
 * @param T
 * @param R
 * @param block
 * @return
 */
inline fun <T, R> T.onNull(block: (T) -> R): R {
    return block(this)
}

/**
 * 设计图默认宽，以便计算比例
 */
private const val UI_DEFAULT_SCREEN = 375

val Float.dp get():Int = this.dpF.toInt()
val Float.dpF get():Float = this * Resources.getSystem().displayMetrics.widthPixels / UI_DEFAULT_SCREEN

val Int.dpF get() = this.dp.toFloat()

/**
 * 和xml的@dimen/dp_5一致（按ui比例来）
 */
val Int.dp get():Int = this * Resources.getSystem().displayMetrics.widthPixels / UI_DEFAULT_SCREEN

fun getCurrentMillis() = System.currentTimeMillis()
