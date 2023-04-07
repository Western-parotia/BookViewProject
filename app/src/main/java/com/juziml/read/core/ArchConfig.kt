package com.juziml.read.core

import android.app.Application
import android.content.ContentProvider
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.foundation.widget.loading.GlobalLoadingConfig
import com.juziml.read.BuildConfig

/**
 * 基础架构 配置类
 * create by zhusw on 5/24/21 17:20
 */
object ArchConfig {

    private var _application: Application? = null

    @JvmStatic
    val app: Application
        get() = _application!!

    /**
     * 预初始化，Application构造调用，这样可以保证任何情况都不为null（[ContentProvider]）
     */
    @JvmStatic
    fun preInitApplication(app: Application) {
        _application = app
        //此处不允许有逻辑
    }

    @JvmStatic
    fun initApplicationOnCreate() {
        GlobalLoadingConfig.onInitForegroundColor = 0xffffffff.toInt()
    }

    /**
     * 记录：非const无法被编译优化掉（开启混淆后可被混淆规则优化掉）
     */
    @JvmField
    val debug: Boolean = BuildConfig.DEBUG

    /**
     * app的版本号
     */
    private fun getHostVersionCode(): Long {
        val pm: PackageManager = app.packageManager
        val pi: PackageInfo = pm.getPackageInfo(
            app.packageName, 0
        )
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            pi.longVersionCode
        } else {
            pi.versionCode.toLong()
        }
    }

    private fun getHostVersionName(): String {
        return try {
            val pm: PackageManager = app.packageManager
            val pi: PackageInfo = pm.getPackageInfo(
                app.packageName, 0
            )
            pi.versionName ?: "unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "unknown"
        }
    }
}