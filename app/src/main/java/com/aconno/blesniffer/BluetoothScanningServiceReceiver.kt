package com.aconno.blesniffer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @author aconno
 */
class BluetoothScanningServiceReceiver(
    private val bluetoothScanningService: BluetoothScanningService
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //TODO: Check intent action.
        val localBroadcastManager =
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(bluetoothScanningService)
        localBroadcastManager.unregisterReceiver(this)
        bluetoothScanningService.stopScanning()
    }
}