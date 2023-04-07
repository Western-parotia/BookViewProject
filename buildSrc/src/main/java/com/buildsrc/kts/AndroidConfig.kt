package com.buildsrc.kts

import org.gradle.api.JavaVersion

object AndroidConfig {
    private val gradleProperties = PropertiesUtils.gradleProperties

    const val compileSdkVersion = 25
    const val minSdkVersion = 24
    const val targetSdkVersion = 25


    object AppInfo {
        @JvmStatic
        val versionName = gradleProperties["version.name"].toString()

        @JvmStatic
        val versionCode = gradleProperties["version.code"].toString().toInt()

        @JvmStatic
        val applicationId = gradleProperties["app.id"].toString()

        const val appName = "Sakura"

        @JvmStatic
        val releaseObfuscate = gradleProperties["switch.release.obfuscate"].toString().toBoolean()

        @JvmStatic
        val debugProguardObfuscate =
            gradleProperties["switch.debugProguard.obfuscate"].toString().toBoolean()
    }

    object Language {
        const val jvmTarget = "1.8"
        val sourceCompatibility = JavaVersion.VERSION_1_8
        val targetCompatibility = JavaVersion.VERSION_1_8
    }
}