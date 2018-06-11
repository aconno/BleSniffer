package com.aconno.acnsensa.dagger.application

import android.arch.persistence.room.Room
import android.bluetooth.BluetoothAdapter
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.IntentProviderImpl
import com.aconno.acnsensa.data.repository.AcnSensaDatabase
import com.aconno.acnsensa.data.repository.DeserializerRepositoryImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.device.bluetooth.BluetoothPermissionImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothStateListener
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.domain.deserializing.DeserializerRepository
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

@Module
class AppModule(private val acnSensaApplication: AcnSensaApplication) {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() =
            LocalBroadcastManager.getInstance(acnSensaApplication.applicationContext)

    @Provides
    @Singleton
    fun provideBluetoothStateReceiver(bluetoothStateListener: BluetoothStateListener) =
            BluetoothStateReceiver(bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothStateListener() = BluetoothStateListener()

    @Provides
    @Singleton
    fun provideBluetooth(
            bluetoothAdapter: BluetoothAdapter,
            bluetoothPermission: BluetoothPermission,
            bluetoothStateListener: BluetoothStateListener
    ): Bluetooth = BluetoothImpl(bluetoothAdapter, bluetoothPermission, bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Provides
    @Singleton
    fun provideBluetoothPermission(): BluetoothPermission = BluetoothPermissionImpl()

    @Provides
    @Singleton
    fun provideAcnSensaApplication(): AcnSensaApplication = acnSensaApplication

    @Provides
    @Singleton
    fun provideScanResultsFlowable(
            bluetooth: Bluetooth
    ): Flowable<ScanResult> {
        return bluetooth.getScanResults()
    }

    @Provides
    @Singleton
    fun provideDeserializerRepository(
            acnSensaDatabase: AcnSensaDatabase
    ): DeserializerRepository {
        return DeserializerRepositoryImpl(acnSensaDatabase.deserializerDao())
    }

    @Provides
    @Singleton
    fun provideAcnSensaDatabase(): AcnSensaDatabase {
        return Room.databaseBuilder(acnSensaApplication, AcnSensaDatabase::class.java, "AcnSensa")
                .fallbackToDestructiveMigration()
                .build()

    }

    @Provides
    @Singleton
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }
}