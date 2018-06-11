package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.viewmodel.ScanResultViewModel
import io.reactivex.Flowable

class ScanResultViewModelFactory(private val scanResults: Flowable<ScanResult>) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ScanResultViewModel(scanResults)
        return getViewModel(viewModel, modelClass)
    }
}