package com.aconno.blesniffer

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.aconno.blesniffer.domain.scanning.Bluetooth
import kotlinx.coroutines.*
import timber.log.Timber

/**
 * @author aconno
 */
class BluetoothScanner(private var context: Context, private var bluetooth : Bluetooth, private var notification: Notification) {
    private var scanTimerDisposable: Job? = null

    /**
     * Restart scanning before Android BLE Scanning Timeout
     */
    private fun startScanningTimer() {
        //Launches non-blocking coroutine
        scanTimerDisposable = GlobalScope.launch(context = Dispatchers.Main) {
            delay(ANDROID_N_MAX_SCAN_DURATION - 60 * 1000)
            Timber.d("Restarting scanning to avoid Android BLE Scanning Timeout")
            restartScanning()
        }
    }

    fun startScanning() {
        startScanningTimer()
        bluetooth.startScanning()
        running = true
    }

    fun stopScanning() {
        scanTimerDisposable?.cancel()

        bluetooth.stopScanning()
        running = false
    }

    private fun restartScanning() {
        stopScanning()
        startScanning()
    }

    companion object {
        private const val ANDROID_N_MAX_SCAN_DURATION =  30 * 60 * 1000L // 30 minutes

        private var running = false

        fun isRunning(): Boolean {
            return running
        }
    }
}