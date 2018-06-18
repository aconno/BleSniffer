package com.aconno.blesniffer.device.bluetooth

interface BluetoothPermission {

    var isGranted: Boolean

    fun request()
}