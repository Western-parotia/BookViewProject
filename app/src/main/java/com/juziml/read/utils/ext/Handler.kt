package com.juziml.read.utils.ext

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 注意：无生命周期，自动移除见[postDelayedLifecycle]
 */
fun Handler.postDelayed(mills: Long, run: Runnable) {
    postDelayed(run, mills)
}

/**
 * destroy时自动移除
 */
fun Handler.postDelayedLifecycle(
    owner: LifecycleOwner,
    mills: Long,
    run: Runnable
) {
    postDelayed(mills, run)
    owner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeCallbacks(run)
            }
        }
    })
}

/////////////////////////////////////////////////////////////////////////////////////////////////
// 全局handler
/////////////////////////////////////////////////////////////////////////////////////////////////

private val globalHandler = Handler(Looper.getMainLooper())

fun removeGlobalRunnable(runnable: Runnable) {
    globalHandler.removeCallbacks(runnable)
}

/**
 * @param deduplication 去重
 * @param run 必须显示指定为Runnable类：Runnable {xxx}
 */
fun postMain(deduplication: Boolean = true, run: Runnable) {
    if (deduplication) {
        globalHandler.removeCallbacks(run)
    }
    globalHandler.post(run)
}

/**
 * 子线程会post，主线程则直接调用
 */
fun postMainSmart(run: Runnable) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        run.run()
    } else {
        globalHandler.post(run)
    }
}

/**
 * 注意：无生命周期，自动移除见[postMainDelayedLifecycle]
 * @param deduplication 是否去重
 */
fun postMainDelayed(mills: Long, deduplication: Boolean = true, run: Runnable) {
    if (deduplication) {
        removeGlobalRunnable(run)
    }
    globalHandler.postDelayed(mills, run)
}

/**
 * destroy时自动移除
 * @param deduplication 是否去重
 */
fun postMainDelayedLifecycle(
    owner: LifecycleOwner,
    mills: Long,
    deduplication: Boolean = true,
    run: Runnable
) {
    if (deduplication) {
        removeGlobalRunnable(run)
    }
    globalHandler.postDelayedLifecycle(owner, mills, run)
}
