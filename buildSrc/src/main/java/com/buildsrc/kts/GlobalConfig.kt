package com.buildsrc.kts

import org.gradle.api.Project

/**
 * 全局初始化project，方便buildSrc全局调用
 */
object GlobalConfig {
    private var _project: Project? = null
    val project: Project get() = _project!!

    val rootDirFile get() = project.rootDir

    @JvmStatic
    fun init(project: Project) {
        _project = project
    }
}