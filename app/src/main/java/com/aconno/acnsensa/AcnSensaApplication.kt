package com.aconno.acnsensa

import android.app.Application
import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.dagger.application.AppModule
import com.aconno.acnsensa.dagger.application.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * @author aconno
 */
class AcnSensaApplication : Application() {

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