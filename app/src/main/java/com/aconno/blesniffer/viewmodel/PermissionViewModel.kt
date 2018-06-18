package com.aconno.blesniffer.viewmodel

import android.content.pm.PackageManager
import com.aconno.blesniffer.device.permissons.PermissionAction
import com.aconno.blesniffer.model.BleSnifferPermission
import java.util.*

/**
 * TODO Refactor // This class has to take multiple permissions at the same time.//
 */
class PermissionViewModel(
        private val permissionAction: PermissionAction,
        private val permissionCallbacks: PermissionCallbacks
) {
    private val runMap: MutableMap<Int, () -> Unit> = mutableMapOf()

    fun requestAccessFineLocation() {
        checkAndRequestPermission(BleSnifferPermission.ACCESS_FINE_LOCATION)
    }

    fun requestReadExternalStoragePermission() {
        checkAndRequestPermission(BleSnifferPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestWriteExternalStoragePermission() {
        checkAndRequestPermission(BleSnifferPermission.WRITE_EXTERNAL_STORAGE)
    }

    fun requestAccessFineLocationAfterRationale() {
        requestPermission(BleSnifferPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessToReadExternalStorage() {
        checkAndRequestPermission(BleSnifferPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestAccessToReadExternalStorageAfterRationale() {
        requestPermission(BleSnifferPermission.READ_EXTERNAL_STORAGE)
    }

    private val random = Random()

    fun checkRequestAndRunIfGranted(bleSnifferPermission: BleSnifferPermission, runnable: () -> Unit) {
        var key = random.nextInt(0xFFFF)
        while(runMap.containsKey(key)) {
            key = random.nextInt(0xFFFF)
        }
        requestPermission(bleSnifferPermission, key)
        runMap[key] = runnable
    }

    fun checkRequestAndRun(bleSnifferPermission: BleSnifferPermission, runnableGranted: () -> Unit, runnableDenied: () -> Unit) {
        var key = random.nextInt(0xFFFF)
        while(runMap.containsKey(key) || runMap.containsKey(key + 1)) {
            key = random.nextInt(0xFFFF)
        }
        requestPermission(bleSnifferPermission, key)
        runMap[key] = runnableGranted
        runMap[key + 1] = runnableDenied
    }

    private fun checkAndRequestPermission(bleSnifferPermission: BleSnifferPermission, code: Int = bleSnifferPermission.code) {
        if (permissionAction.hasSelfPermission(bleSnifferPermission.permission)) {
            permissionCallbacks.permissionAccepted(code)
        } else {
            if (permissionAction.shouldShowRequestPermissionRationale(bleSnifferPermission.permission)) {
                requestPermission(bleSnifferPermission)
            } else {
                requestPermission(bleSnifferPermission)
            }
        }
    }

    private fun requestPermission(bleSnifferPermission: BleSnifferPermission, code: Int = bleSnifferPermission.code) {
        permissionAction.requestPermission(bleSnifferPermission.permission, code)
    }

    fun checkGrantedPermission(grantResults: IntArray, requestCode: Int) {
        if (verifyGrantedPermission(grantResults)) {
            runMap[requestCode]?.invoke()
            runMap.remove(requestCode)
            permissionCallbacks.permissionAccepted(requestCode)
        } else {
            runMap[requestCode + 1]?.invoke()
            runMap.remove(requestCode + 1)
            permissionCallbacks.permissionDenied(requestCode)
        }
    }

    fun hasSelfPermission(bleSnifferPermission: BleSnifferPermission): Boolean {
        return permissionAction.hasSelfPermission(bleSnifferPermission.permission)
    }

    private fun verifyGrantedPermission(grantResults: IntArray): Boolean {
        grantResults.forEach {
            if (it != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    interface PermissionCallbacks {

        fun permissionAccepted(actionCode: Int)

        fun permissionDenied(actionCode: Int)

        fun showRationale(actionCode: Int)
    }
}