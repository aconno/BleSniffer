package com.aconno.blesniffer

import android.app.Application
import com.aconno.blesniffer.dagger.application.AppComponent
import com.aconno.blesniffer.dagger.application.AppModule
import com.aconno.blesniffer.dagger.application.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * @author aconno
 */
class BleSnifferApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())
    }
}