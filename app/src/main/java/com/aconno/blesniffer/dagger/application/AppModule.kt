package com.aconno.blesniffer.dagger.application

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import android.bluetooth.BluetoothAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothStateReceiver
import com.aconno.blesniffer.IntentProviderImpl
import com.aconno.blesniffer.data.repository.BleSnifferDatabase
import com.aconno.blesniffer.data.repository.DeserializerRepositoryImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothPermission
import com.aconno.blesniffer.device.bluetooth.BluetoothPermissionImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothStateListener
import com.aconno.blesniffer.device.notification.IntentProvider
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

@Module
class AppModule(private val bleSnifferApplication: BleSnifferApplication) {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() =
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(bleSnifferApplication.applicationContext)

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
    fun provideBleSnifferApplication(): BleSnifferApplication = bleSnifferApplication

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
            bleSnifferDatabase: BleSnifferDatabase
    ): DeserializerRepository {
        return DeserializerRepositoryImpl(bleSnifferDatabase.deserializerDao())
    }

    @Provides
    @Singleton
    fun provideBleSnifferDatabase(): BleSnifferDatabase {
        return Room.databaseBuilder(bleSnifferApplication, BleSnifferDatabase::class.java, "BleSniffer")
                .addMigrations(object : Migration(9, 11) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE deserializers ADD COLUMN sampleData BLOB NOT NULL")
                    }

                })
                .fallbackToDestructiveMigration()
                .build()

    }

    @Provides
    @Singleton
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }
}