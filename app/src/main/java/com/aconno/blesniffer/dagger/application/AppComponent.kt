package com.aconno.blesniffer.dagger.application

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothStateReceiver
import com.aconno.blesniffer.dagger.work.WorkerModule
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.domain.sync.SyncRepository
import com.aconno.blesniffer.work.factory.BleSnifferWorkerFactory
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

@Component(modules = [AppModule::class, WorkerModule::class])
@Singleton
interface AppComponent {

    fun bleSnifferWorkerFactory(): BleSnifferWorkerFactory

    fun bleSnifferApplication(): BleSnifferApplication

    fun bluetooth(): Bluetooth

    fun scanResults(): Flowable<ScanResult>

    fun deserializerRepository(): DeserializerRepository

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    fun syncRepository(): SyncRepository
}