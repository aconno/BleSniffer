package com.aconno.blesniffer.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothScanner
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.domain.model.ScanEvent
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import timber.log.Timber

//TODO: This needs refactoring.
/**
 * @aconno
 */
class BluetoothScanningViewModel(
    private val bluetooth: Bluetooth, application: BleSnifferApplication,
    private val bluetoothScanner: BluetoothScanner
) : AndroidViewModel(application) {

    private val result: MutableLiveData<ScanEvent> = MutableLiveData()
    private var bluetoothObservableDisposable : Disposable? = null

    private fun subscribe() {
        val observable: Flowable<ScanEvent> = bluetooth.getScanEvents()
        bluetoothObservableDisposable = observable.subscribe { result.value = it }
    }

    fun startScanning() {
        Timber.d("startScanning")
        subscribe()
        bluetoothScanner.startScanning()
    }

    fun stopScanning() {
        Timber.d("stopScanning")
        bluetoothScanner.stopScanning()

        bluetoothObservableDisposable?.dispose()
    }

    fun getResult(): MutableLiveData<ScanEvent> {
        return result
    }
}