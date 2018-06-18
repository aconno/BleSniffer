package com.aconno.blesniffer.dagger.bluetoothscanning

import com.aconno.blesniffer.BluetoothScanningService
import com.aconno.blesniffer.dagger.application.AppComponent
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [BluetoothScanningServiceModule::class])
@BluetoothScanningServiceScope
interface BluetoothScanningServiceComponent {

    fun inject(bluetoothScanningService: BluetoothScanningService)
}

