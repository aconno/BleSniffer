package com.aconno.blesniffer

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import com.aconno.blesniffer.dagger.bluetoothscanning.BluetoothScanningServiceComponent
import com.aconno.blesniffer.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.blesniffer.dagger.bluetoothscanning.DaggerBluetoothScanningServiceComponent
import com.aconno.blesniffer.domain.scanning.Bluetooth
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import javax.inject.Inject

/**
 * @author aconno
 */
class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var receiver: BroadcastReceiver

    @Inject
    lateinit var filter: IntentFilter

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager


    private val bluetoothScanningServiceComponent: BluetoothScanningServiceComponent by lazy {
        val bleSnifferApplication: BleSnifferApplication? = application as? BleSnifferApplication
        DaggerBluetoothScanningServiceComponent.builder()
                .appComponent(bleSnifferApplication?.appComponent)
                .bluetoothScanningServiceModule(BluetoothScanningServiceModule(this))
                .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothScanningServiceComponent.inject(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)

        bluetooth.startScanning()
        running = true
        return START_STICKY
    }

    fun stopScanning() {
        bluetooth.stopScanning()
        running = false
        stopSelf()
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, BluetoothScanningService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private var running = false

        fun isRunning(): Boolean {
            return running
        }
    }
}