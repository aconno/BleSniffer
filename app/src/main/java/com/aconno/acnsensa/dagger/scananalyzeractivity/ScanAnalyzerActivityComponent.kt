package com.aconno.acnsensa.dagger.scananalyzeractivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.ScanAnalyzerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [ScanAnalyzerActivityModule::class])
@ScanAnalyzerActivityScope
interface ScanAnalyzerActivityComponent {
    fun inject(mainActivity: ScanAnalyzerActivity)
}