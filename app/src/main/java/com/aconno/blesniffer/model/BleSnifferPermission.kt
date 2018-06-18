package com.aconno.blesniffer.model

import android.Manifest

class BleSnifferPermission(val code: Int, val permission: String) {

    companion object {

        const val ACCESS_FINE_LOCATION_CODE = 1
        const val READ_EXTERNAL_STORAGE_CODE = 2
        const val WRITE_EXTERNAL_STORAGE_CODE = 3

        val ACCESS_FINE_LOCATION =
                BleSnifferPermission(ACCESS_FINE_LOCATION_CODE, Manifest.permission.ACCESS_FINE_LOCATION)
        val READ_EXTERNAL_STORAGE =
                BleSnifferPermission(READ_EXTERNAL_STORAGE_CODE, Manifest.permission.READ_EXTERNAL_STORAGE)
        val WRITE_EXTERNAL_STORAGE =
                BleSnifferPermission(WRITE_EXTERNAL_STORAGE_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}