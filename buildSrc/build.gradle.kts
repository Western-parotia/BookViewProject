buildscript {
    repositories {
        //阿里镜像地址：https://developer.aliyun.com/mvn/guide
        maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
    }
}

plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
}

repositories {
    maven { setUrl("https://maven.aliyun.com/nexus/content/groups/public/") }
    maven { setUrl("https://maven.aliyun.com/repository/google") }
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("com.android.tools.build:gradle:4.1.0")
    implementation(localGroovy())
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
