package com.juziml.read.core

import android.app.Application
import android.content.Intent
import android.os.Bundle

/**
 * create by zhusw on 6/7/21 14:16
 */
open class ArchApplication : Application() {
    init {
        @Suppress("LeakingThis")//里面什么都没做，所以抑制警告
        (ArchConfig.preInitApplication(this))
    }


    override fun onCreate() {
        super.onCreate()
        //ArchConfig 需要晚于Utils 初始化
        ArchConfig.initApplicationOnCreate()

    }

    override fun startActivity(intent: Intent) {
        startActivity(intent, null)
    }

    override fun startActivity(intent: Intent, options: Bundle?) {
        //默认增加new task标识
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        super.startActivity(intent, options)
    }
}