package com.aconno.acnsensa.viewmodel

import android.content.pm.PackageManager
import com.aconno.acnsensa.device.permissons.PermissionAction
import com.aconno.acnsensa.model.AcnSensaPermission
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
        checkAndRequestPermission(AcnSensaPermission.ACCESS_FINE_LOCATION)
    }

    fun requestReadExternalStoragePermission() {
        checkAndRequestPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestWriteExternalStoragePermission() {
        checkAndRequestPermission(AcnSensaPermission.WRITE_EXTERNAL_STORAGE)
    }

    fun requestAccessFineLocationAfterRationale() {
        requestPermission(AcnSensaPermission.ACCESS_FINE_LOCATION)
    }

    fun requestAccessToReadExternalStorage() {
        checkAndRequestPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)
    }

    fun requestAccessToReadExternalStorageAfterRationale() {
        requestPermission(AcnSensaPermission.READ_EXTERNAL_STORAGE)
    }

    private val random = Random()

    fun checkRequestAndRunIfGranted(acnSensaPermission: AcnSensaPermission, runnable: () -> Unit) {
        var key = random.nextInt(0xFFFF)
        while(runMap.containsKey(key)) {
            key = random.nextInt(0xFFFF)
        }
        requestPermission(acnSensaPermission, key)
        runMap[key] = runnable
    }

    fun checkRequestAndRun(acnSensaPermission: AcnSensaPermission, runnableGranted: () -> Unit, runnableDenied: () -> Unit) {
        var key = random.nextInt(0xFFFF)
        while(runMap.containsKey(key) || runMap.containsKey(key + 1)) {
            key = random.nextInt(0xFFFF)
        }
        requestPermission(acnSensaPermission, key)
        runMap[key] = runnableGranted
        runMap[key + 1] = runnableDenied
    }

    private fun checkAndRequestPermission(acnSensaPermission: AcnSensaPermission, code: Int = acnSensaPermission.code) {
        if (permissionAction.hasSelfPermission(acnSensaPermission.permission)) {
            permissionCallbacks.permissionAccepted(code)
        } else {
            if (permissionAction.shouldShowRequestPermissionRationale(acnSensaPermission.permission)) {
                requestPermission(acnSensaPermission)
            } else {
                requestPermission(acnSensaPermission)
            }
        }
    }

    private fun requestPermission(acnSensaPermission: AcnSensaPermission, code: Int = acnSensaPermission.code) {
        permissionAction.requestPermission(acnSensaPermission.permission, code)
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

    fun hasSelfPermission(acnSensaPermission: AcnSensaPermission): Boolean {
        return permissionAction.hasSelfPermission(acnSensaPermission.permission)
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