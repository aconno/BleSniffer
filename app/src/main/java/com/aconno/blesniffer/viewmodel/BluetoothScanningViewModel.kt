package com.aconno.blesniffer.viewmodel

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothScanningService
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.domain.model.ScanEvent
import io.reactivex.Flowable
import timber.log.Timber

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: BleSnifferApplication
) : AndroidViewModel(application) {

    private val result: MutableLiveData<ScanEvent> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {
        val observable: Flowable<ScanEvent> = bluetooth.getScanEvents()
        observable.subscribe { result.value = it }
    }

    fun startScanning() {
        Timber.d("startScanning")
        BluetoothScanningService.start(getApplication())
    }

    fun stopScanning() {
        Timber.d("stopScanning")

        val localBroadcastManager = LocalBroadcastManager.getInstance(getApplication())
        localBroadcastManager.sendBroadcast(Intent("com.aconno.blesniffer.STOP"))
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }
}