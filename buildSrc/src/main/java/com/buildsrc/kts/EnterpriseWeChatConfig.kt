package com.buildsrc.kts

object EnterpriseWeChatConfig {
    const val updateDesc =
        """因低版本Inflater崩溃，临时移除Inflater的hook
        """

    @JvmStatic
    val atUsers = listOf("WangNeng")


    const val webHookUrl =
        "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=4c7a799c-f4bb-4aa9-93cd-a934d4655b0b"
}