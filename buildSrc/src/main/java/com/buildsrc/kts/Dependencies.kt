package com.buildsrc.kts

import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 *@Desc:
 *-
 *-依赖声明
 *create by zhusw on 5/6/21 15:45
 */
object Dependencies {

    object Kotlin {
        const val version = "1.6.21"

        /**
         * kotlin 语言核心库，像 let,isEmpty 这种工具函数
         */
        const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"

    }

    object FlutterModule {
        const val recommendCustomerListPage =
            "com.mj.flutter.rcp:flutter_release:1.0.4"
    }

    object Foundation {
        const val activityFragment = "com.foundation.app:activity-fragment:1.1.1"
        const val net = "com.foundation.service:net:1.0.6"
        const val loadingView = "com.foundation.widget:loadingview:1.5.0"
        const val recyclerviewAdapter =
            "com.foundation.widget:convenient-recyclerview-adapter:1.1.0"
        const val shape = "com.foundation.widget:shape-view:1.1.2"
        const val roundedImageView = "com.foundation.widget:rounded-image-view:1.0.1"
        const val baseDialog = "com.foundation.app:base-dialog:1.0.9"
        const val radioGroup = "com.foundation.widget:radio-group:1.0.2"
        const val stickyLayout = "com.foundation.widget:sticky-layout:1.0.7"
        const val popupWindow = "com.foundation.app:base-popupwindow:1.0.5"
        const val toast = "com.foundation.widget:Toast:1.0.4"
        const val pictureSelector = "com.foundation.widget:picture-selector:1.0.5"
        const val sp = "com.foundation.service:sp:1.0.8"

        /**
         * application 初始化管理
         * activityStack
         */
        const val initManager = "com.foundation.app:manager:2.0.1"
        const val json = "com.foundation.service:json:2.0.1"
        const val viewBindingHelper = "com.foundation.widget:view-binding-helper:1.0.1"
        const val annotation = "com.foundation.service:prouter-annotations:0.0.4"
        const val processor = "com.foundation.service:prouter-processor:0.0.4"
        const val loggerInterceptor = "com.foundation.service:logger-interceptor:1.0.6"
        const val debugDialog = "com.foundation.debug:debug-dialog:1.1.4"
        const val debugHandler = "com.foundation.debug:debug-handler:1.0.1"
        const val permission = "com.foundation.service:permission:1.0.4"
        const val deviceUtils = "com.foundation.widget:DeviceUtils:1.0.0"
        const val messageBus = "com.foundation.service:message-bus:1.0.2"
        const val sliderVerifyView = "com.foundation.service:slider-verify-view:1.0.0"

        /**
         * 知乎图片选择
         */
        const val zhihuMatisse = "com.foundation.widget:zhihu-matisse:1.0.4"

        /**
         * 异常统计上报
         */
        const val exceptionReport = "com.foundation.service:exception-report:1.0.3"

        const val web = "com.foundation.widget:web:1.0.6"
        const val utils = "com.foundation.widget:utils:1.0.2"
    }

    object AndroidX {
        private const val roomVersion = "2.3.0"

        /**
         * kotlin 标准库，各种拓展方法，像 foreach什么的
         */
        const val core_ktx = "androidx.core:core-ktx:1.3.2"
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"
        const val room_runtime = "androidx.room:room-runtime:$roomVersion"
        const val room_ktx = "androidx.room:room-ktx:$roomVersion"
        const val room_compiler = "androidx.room:room-compiler:$roomVersion"
    }


    object Material {
        /**
         * Material 样式库，包含对应的view widget 与 style
         */
        const val material = "com.google.android.material:material:1.3.0"
    }

    object OpenSourceLibrary {

        const val flex_box = "com.google.android:flexbox:2.0.1"
        const val materialEdittext = "com.rengwuxian.materialedittext:library:2.1.4"
        const val banner = "com.github.zhpanvip:BannerViewPager:3.5.0"
        private const val smartVersion = "2.0.5"
        const val smartRefreshLayout = "io.github.scwang90:refresh-layout-kernel:$smartVersion"
        const val smartRefreshLayoutHeader =
            "io.github.scwang90:refresh-header-material:$smartVersion"
        const val smartRefreshLayoutFooter =
            "io.github.scwang90:refresh-footer-classics:$smartVersion"

        const val glide = "com.github.bumptech.glide:glide:4.13.2"
        const val aliyun_file_upload = "com.aliyun.dpa:oss-android-sdk:2.9.5"
        const val live_event_bus_x = "io.github.jeremyliao:live-event-bus-x:1.8.0"

        const val chunkDebug = "com.github.chuckerteam.chucker:library:3.5.0"
        const val chunkRelease = "com.github.chuckerteam.chucker:library-no-op:3.5.0"
        const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.7"

        const val retorifit = "com.squareup.retrofit2:retrofit:2.9.0"//依赖okhttp 3.14.9
        const val retorifitAdapterRxJava =
            "com.squareup.retrofit2:adapter-rxjava:2.9.0"//retorifit支持RxJava

        const val junit = "junit:junit:4.13.2"
        const val eventBus = "org.greenrobot:eventbus:3.3.1"
        const val rxAndroid = "io.reactivex:rxandroid:1.2.1"

        //新版为2.3.0，api改动较大暂不升级
        const val photoView = "com.github.chrisbanes:PhotoView:1.3.1"

        //城市选择滑动控件，和module重复，待后期合并删除，新版4.1.9，改动较大并且于19年停止维护
        const val pickerView = "com.contrarywind:Android-PickerView:3.2.7"
        const val zxingLibrary = "cn.yipianfengye.android:zxing-library:2.2"
        const val XTabLayout = "com.androidkun:XTabLayout:1.1.5"

        //萤石直播依赖库，4.19.9改动较大暂不升级
        const val ezviz = "io.github.ezviz-open:ezviz-sdk:4.16.6.3"

        //视频压缩
        const val videoProcessor = "com.github.yellowcath:VideoProcessor:2.4.2"

        //数据库工具类，分类用到（无用代码，待删除）
        const val litepal = "org.litepal.android:core:1.6.1"

        //饺子视屏播放
        const val jiaoZiVideoPlayer = "cn.jzvd:jiaozivideoplayer:7.7.2.3300"

        //pdf预览
        const val pdfViewPager = "es.voghdev.pdfviewpager:library:1.1.3"

        //微信
        const val WeChat = "com.tencent.mm.opensdk:wechat-sdk-android-without-mta:6.8.0"

        //神策
        const val sensors = "com.sensorsdata.analytics.android:SensorsAnalyticsSDK:6.4.2"

        //侧滑删除库
        const val swipeLayout = "com.daimajia.swipelayout:library:1.2.0@aar"

        const val unpeekLiveData = "com.kunminx.arch:unpeek-livedata:6.1.0-beta1"
    }

    object Chucker {
        const val chuckerDebug = "com.github.chuckerteam.chucker:library:3.5.2"
        const val chuckerRelease = "com.github.chuckerteam.chucker:library-no-op:3.5.2"
    }

    object ButterKnife {
        const val butterKnife = "com.jakewharton:butterknife:10.2.3"
        const val butterKnifeCompiler = "com.jakewharton:butterknife-compiler:10.2.3"
    }

    object Google {
        //谷歌更强大的flowLayout>FlexboxLayout和对应的Manager
        const val flexBox = "com.google.android:flexbox:2.0.1"
    }

    object AMap {
        const val map = "com.amap.api:map2d:6.0.0" //2D地图
        const val location = "com.amap.api:location:6.1.0" //定位功能
        const val search = "com.amap.api:search:9.3.1" //搜索功能
    }

    object Umeng {
        const val umeng_common = "com.umeng.umsdk:common:9.5.0"
        const val umeng_asms = "com.umeng.umsdk:asms:1.6.3"

        //友盟Push依赖
        const val umeng_push = "com.umeng.umsdk:push:6.5.3"

        //性能、异常监控
        const val umeng_apm = "com.umeng.umsdk:apm:1.7.0"

        const val huaweiUmeng = "com.umeng.umsdk:huawei-umengaccs:1.3.5"
        const val huaweiPush = "com.huawei.hms:push:5.3.0.304"

        const val xiaomiUmeng = "com.umeng.umsdk:xiaomi-umengaccs:1.2.6"
        const val xiaomiPush = "com.umeng.umsdk:xiaomi-push:4.8.1"

        const val vivoUmeng = "com.umeng.umsdk:vivo-umengaccs:1.1.5"
        const val vivoPush = "com.umeng.umsdk:vivo-push:3.0.0.3"

        const val oppoUmeng = "com.umeng.umsdk:oppo-umengaccs:1.0.7-fix"
        const val oppoPush = "com.umeng.umsdk:oppo-push:2.1.0"

        const val meizuUmeng = "com.umeng.umsdk:meizu-umengaccs:1.1.5"
        const val meizuPush = "com.umeng.umsdk:meizu-push:4.1.4"
    }

    object Ali {
        //热修复
        const val hotfix = "com.aliyun.ams:alicloud-android-hotfix:3.3.7"


        const val haAdapter = "com.aliyun.ams:alicloud-android-ha-adapter:1.1.5.2-open"
        const val apm = "com.aliyun.ams:alicloud-android-apm:1.1.0.0-open"
    }
}

/**
 * @param dependencies 第三方库字符串或module
 */
fun DependencyHandler.debugProguardImplementation(dependencies: Any) {
    add("debugProguardImplementation", dependencies)
}