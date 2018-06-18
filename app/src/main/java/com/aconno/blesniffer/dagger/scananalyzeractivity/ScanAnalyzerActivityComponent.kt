package com.aconno.blesniffer.dagger.scananalyzeractivity

import com.aconno.blesniffer.dagger.application.AppComponent
import com.aconno.blesniffer.ui.ScanAnalyzerActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [ScanAnalyzerActivityModule::class])
@ScanAnalyzerActivityScope
interface ScanAnalyzerActivityComponent {
    fun inject(mainActivity: ScanAnalyzerActivity)
}