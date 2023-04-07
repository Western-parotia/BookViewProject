package com.juziml.read.utils.ext

import android.util.SparseArray

/**
 * 如果没有则put
 */
inline fun <V> SparseArray<V>.getOrPut(key: Int, defaultValue: () -> V): V {
    val value = get(key)
    return if (value == null) {
        val answer = defaultValue()
        put(key, answer)
        answer
    } else {
        value
    }
}