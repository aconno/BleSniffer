package com.aconno.blesniffer.viewmodel.factory

import androidx.lifecycle.ViewModel
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothScanner
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.viewmodel.BluetoothScanningViewModel

/**
 * @author aconno
 */
class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val bleSnifferApplication: BleSnifferApplication,
    private val bluetoothScanner : BluetoothScanner
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothScanningViewModel(bluetooth, bleSnifferApplication,bluetoothScanner)
        return getViewModel(viewModel, modelClass)
    }
}