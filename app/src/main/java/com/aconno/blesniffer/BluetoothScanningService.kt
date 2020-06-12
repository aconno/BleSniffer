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
import kotlinx.coroutines.*
import timber.log.Timber
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

    private lateinit var scanTimerDisposable: Job


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

        startScanning()
        return START_STICKY
    }

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

    private fun startScanning() {
        startScanningTimer()
        bluetooth.startScanning()
        running = true
    }

    fun stopScanning(stopService : Boolean = true) {
        scanTimerDisposable.cancel()

        bluetooth.stopScanning()
        running = false

        if(stopService) {
            stopSelf()
        }
    }

    private fun restartScanning() {
        stopScanning(stopService = false)
        startScanning()
    }

    companion object {
        private const val ANDROID_N_MAX_SCAN_DURATION =  30 * 60 * 1000L // 30 minutes

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