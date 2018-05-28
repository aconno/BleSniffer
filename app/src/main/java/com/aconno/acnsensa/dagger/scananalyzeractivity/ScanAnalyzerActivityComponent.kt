package com.aconno.acnsensa.dagger.scananalyzeractivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.BeaconListFragment
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.ui.ScanAnalyzerActivity
import com.aconno.acnsensa.ui.SensorListFragment
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [ScanAnalyzerActivityModule::class])
@ScanAnalyzerActivityScope
interface ScanAnalyzerActivityComponent {
    fun inject(mainActivity: ScanAnalyzerActivity)
    fun inject(sensorListFragment: SensorListFragment)
    fun inject(beaconListFragment: BeaconListFragment)
}