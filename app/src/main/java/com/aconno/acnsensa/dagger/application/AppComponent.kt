package com.aconno.acnsensa.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.scanning.Bluetooth
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun acnSensaApplication(): AcnSensaApplication

    fun bluetooth(): Bluetooth

    fun scanResults(): Flowable<ScanResult>

    fun deserializerRepository(): DeserializerRepository

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager
}