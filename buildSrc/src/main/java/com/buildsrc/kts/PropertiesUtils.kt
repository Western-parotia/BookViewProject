package com.buildsrc.kts

import com.android.SdkConstants
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

object PropertiesUtils {

    val gradleProperties =
        getFileProperties(File(GlobalConfig.rootDirFile, SdkConstants.FN_GRADLE_PROPERTIES))

    /**
     * local可能没有，如：远程构建
     */
    val localProperties by lazy(LazyThreadSafetyMode.PUBLICATION) {
        getFileProperties(File(GlobalConfig.rootDirFile, SdkConstants.FN_LOCAL_PROPERTIES))
    }

    /**
     * 读取properties文件
     */
    fun getFileProperties(file: File): Properties {
        val properties = Properties()
        InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
        return properties
    }
}