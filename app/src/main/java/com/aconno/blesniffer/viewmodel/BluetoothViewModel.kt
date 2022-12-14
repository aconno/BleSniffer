package com.aconno.blesniffer.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import com.aconno.blesniffer.BluetoothStateReceiver
import com.aconno.blesniffer.SingleLiveEvent
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.domain.scanning.BluetoothState
import io.reactivex.disposables.Disposable

class BluetoothViewModel(
    private val bluetooth: Bluetooth,
    private val bluetoothStateReceiver: BluetoothStateReceiver,
    private val application: Application
) : ViewModel() {

    val bluetoothState: MutableLiveData<BluetoothState> = SingleLiveEvent()

    private var bluetoothStatesSubscription: Disposable? = null

    @SuppressLint("MissingPermission")
    fun enableBluetooth(context: Context) {
        val bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        bluetoothAdapter.enable()
    }

    fun isBluetoothAvailable() = BluetoothAdapter.getDefaultAdapter() != null

    fun observeBluetoothState() {
        application.applicationContext.registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        val bluetoothStates = bluetooth.getStateEvents()
        bluetoothStatesSubscription = bluetoothStates.subscribe { bluetoothState.value = it }
    }

    fun stopObservingBluetoothState() {
        application.applicationContext.unregisterReceiver(bluetoothStateReceiver)
        bluetoothStatesSubscription?.dispose()
    }
}