package com.juziml.read.utils.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 创建一个临时的LiveData回调，可实现类似1对1的网络请求回调
 */
inline fun <reified T> LifecycleOwner.newLiveDataCallback(obs: Observer<T>): MutableLiveData<T> {
    return MutableLiveData<T>().apply { observe(this@newLiveDataCallback, obs) }
}