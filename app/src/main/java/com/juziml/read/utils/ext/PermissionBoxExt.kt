package com.juziml.read.utils.ext

import android.Manifest
import com.foundation.service.permission.PermissionBox

/**
 * 读写权限
 */
@JvmOverloads
fun PermissionBox.Builder.requestReadWriter(
    leastOneReject: () -> Unit = { "读写权限被拒绝".toast() },
    allGranted: () -> Unit
) {
    setRequestPermission(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    setlLeastOneReject { _, _ -> leastOneReject.invoke() }
    startRequest(allGranted)
}

/**
 * 相机和读写权限
 */
@JvmOverloads
fun PermissionBox.Builder.requestCameraAndReadWriter(
    leastOneReject: () -> Unit = { "相机和读写权限被拒绝".toast() },
    allGranted: () -> Unit
) {
    setRequestPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    setlLeastOneReject { _, _ -> leastOneReject.invoke() }
    startRequest(allGranted)
}

/**
 * 相机权限
 */
@JvmOverloads
fun PermissionBox.Builder.requestCamera(
    leastOneReject: () -> Unit = { "相机权限被拒绝".toast() },
    allGranted: () -> Unit
) {
    setRequestPermission(Manifest.permission.CAMERA)
    setlLeastOneReject { _, _ -> leastOneReject.invoke() }
    startRequest(allGranted)
}

/**
 * 位置权限
 * 注意：[onGranted]里有isFineLocation（是否是精确定位）（原因：Android12可以不给精确位置）
 * （如果isFineLocation是false，你可以继续申请，用户也可以继续或者永远拒绝）
 */
@JvmOverloads
fun PermissionBox.Builder.requestLocation(
    failedCall: () -> Unit = { "定位权限被拒绝".toast() },
    onGranted: (isFineLocation: Boolean) -> Unit
) {
    setRequestPermission(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    )
    setlLeastOneReject { grantedList, _ ->
        if (grantedList.contains { it.name == Manifest.permission.ACCESS_COARSE_LOCATION }) {
            onGranted.invoke(false)
        } else {
            failedCall.invoke()
        }
    }
    startRequest { onGranted.invoke(true) }
}

/**
 * 拨号权限
 */
@JvmOverloads
fun PermissionBox.Builder.requestCallPhone(
    leastOneReject: () -> Unit = { "拨号权限被拒绝".toast() },
    allGranted: () -> Unit
) {
    setRequestPermission(Manifest.permission.CALL_PHONE)
    setlLeastOneReject { _, _ -> leastOneReject.invoke() }
    startRequest(allGranted)
}

/**
 * 读通讯录权限
 */
@JvmOverloads
fun PermissionBox.Builder.requestReadContacts(
    leastOneReject: () -> Unit = { "通讯录权限被拒绝".toast() },
    allGranted: () -> Unit
) {
    setRequestPermission(Manifest.permission.READ_CONTACTS)
    setlLeastOneReject { _, _ -> leastOneReject.invoke() }
    startRequest(allGranted)
}