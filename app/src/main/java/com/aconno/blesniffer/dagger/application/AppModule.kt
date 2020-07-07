package com.aconno.blesniffer.dagger.application

import android.bluetooth.BluetoothAdapter
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.aconno.blesniffer.BleSnifferApplication
import com.aconno.blesniffer.BluetoothStateReceiver
import com.aconno.blesniffer.IntentProviderImpl
import com.aconno.blesniffer.data.remote.FormatApiService
import com.aconno.blesniffer.data.remote.mappers.DeserializerMapper
import com.aconno.blesniffer.data.repository.BleSnifferDatabase
import com.aconno.blesniffer.data.repository.DeserializerRepositoryImpl
import com.aconno.blesniffer.data.repository.mappers.DeserializerEntityMapper
import com.aconno.blesniffer.data.sync.SyncRepositoryImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothPermission
import com.aconno.blesniffer.device.bluetooth.BluetoothPermissionImpl
import com.aconno.blesniffer.device.bluetooth.BluetoothStateListener
import com.aconno.blesniffer.device.notification.IntentProvider
import com.aconno.blesniffer.domain.deserializing.DeserializerRepository
import com.aconno.blesniffer.domain.interactor.sync.SyncDeserializersUseCase
import com.aconno.blesniffer.domain.model.ScanResult
import com.aconno.blesniffer.domain.scanning.Bluetooth
import com.aconno.blesniffer.domain.sync.SyncRepository
import com.aconno.blesniffer.preferences.BleSnifferPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val bleSnifferApplication: BleSnifferApplication) {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() =
        LocalBroadcastManager.getInstance(bleSnifferApplication.applicationContext)

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
    fun provideBleSnifferPreferences(): BleSnifferPreferences = BleSnifferPreferences(bleSnifferApplication)

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
        bleSnifferDatabase: BleSnifferDatabase, deserializerEntityMapper: DeserializerEntityMapper
    ): DeserializerRepository {
        return DeserializerRepositoryImpl(
            bleSnifferDatabase.deserializerDao(),
            deserializerEntityMapper
        )
    }

    @Provides
    @Singleton
    fun provideDeserializerEntityMapper(): DeserializerEntityMapper {
        return DeserializerEntityMapper()
    }

    @Provides
    @Singleton
    fun provideBleSnifferDatabase(): BleSnifferDatabase {
        return Room.databaseBuilder(
            bleSnifferApplication,
            BleSnifferDatabase::class.java,
            "BleSniffer"
        )
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

    @Provides
    fun provideSyncDeserializersUseCase(syncRepository: SyncRepository) = SyncDeserializersUseCase(syncRepository)

    @Provides
    fun provideSyncRepository(
        formatApiService: FormatApiService,
        sharedPreferences: SharedPreferences,
        deserializerMapper: DeserializerMapper,
        deserializerEntityMapper: DeserializerEntityMapper,
        bleSnifferDatabase: BleSnifferDatabase
    ): SyncRepository {
        return SyncRepositoryImpl(
            formatApiService,
            sharedPreferences,
            deserializerMapper,
            deserializerEntityMapper,
            bleSnifferDatabase.deserializerDao()
        )
    }

    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(bleSnifferApplication.applicationContext)
    }

    @Provides
    fun provideDeserializerMapper(): DeserializerMapper {
        return DeserializerMapper()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(FormatApiService.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideFormatApiService(retrofit: Retrofit): FormatApiService {
        return retrofit.create(FormatApiService::class.java)
    }

    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
        return httpClient.build()
    }

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor {
            Timber.d(it)
        }.also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
    }
}