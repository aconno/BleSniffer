package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import io.reactivex.Flowable
import timber.log.Timber

class ScanResultViewModel(
    private val scanResults: Flowable<ScanResult>
) : ViewModel() {

    private val scanResultsLiveData: MutableLiveData<ScanResult> = MutableLiveData()

    init {
        scanResults.subscribe{
            scanResultsLiveData.value = it
        }
    }

    fun getScanResultsLiveData(): MutableLiveData<ScanResult> {
        return scanResultsLiveData
    }
}