package com.aconno.acnsensa.dagger.bluetoothscanning

import com.aconno.acnsensa.BluetoothScanningService
import com.aconno.acnsensa.dagger.application.AppComponent
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [BluetoothScanningServiceModule::class])
@BluetoothScanningServiceScope
interface BluetoothScanningServiceComponent {

    fun inject(bluetoothScanningService: BluetoothScanningService)
}

