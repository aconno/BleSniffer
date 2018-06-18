package com.aconno.blesniffer.device.bluetooth

/**
 * @author aconno
 */
class BluetoothPermissionImpl : BluetoothPermission {
    override var isGranted: Boolean = true

    override fun request() {
        //Do nothing.
    }
}