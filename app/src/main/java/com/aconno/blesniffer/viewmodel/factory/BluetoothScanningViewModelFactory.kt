package com.aconno.blesniffer.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.viewmodel.BluetoothScanningViewModel

/**
 * @author aconno
 */
class BluetoothScanningViewModelFactory(
    private val bluetooth: Bluetooth,
    private val bleSnifferApplication: BleSnifferApplication
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BluetoothScanningViewModel(bluetooth, bleSnifferApplication)
        return getViewModel(viewModel, modelClass)
    }
}