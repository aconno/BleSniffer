package com.aconno.blesniffer

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aconno.blesniffer.dagger.application.AppComponent
import com.aconno.blesniffer.dagger.application.AppModule
import com.aconno.blesniffer.dagger.application.DaggerAppComponent
import com.aconno.blesniffer.work.SyncDeserializersWorker
import com.aconno.blesniffer.work.factory.BleSnifferWorkerFactory

import timber.log.Timber


/**
 * @author aconno
 */
class BleSnifferApplication : Application() {

    lateinit var bleSnifferWorkerFactory: BleSnifferWorkerFactory

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        bleSnifferWorkerFactory = appComponent.bleSnifferWorkerFactory()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initAndStartSyncWorker()
    }

    private fun initAndStartSyncWorker() {
        val configuration = Configuration.Builder()
            .setWorkerFactory(bleSnifferWorkerFactory)
            .build()

        WorkManager.initialize(
            this,
            configuration
        )

        SyncDeserializersWorker.createAndEnqueue()
    }
}