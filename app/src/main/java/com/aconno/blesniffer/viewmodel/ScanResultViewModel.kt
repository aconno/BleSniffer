package com.aconno.blesniffer.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.blesniffer.domain.model.Device
import com.aconno.blesniffer.domain.model.ScanResult
import io.reactivex.Flowable
import timber.log.Timber

class ScanResultViewModel(
    scanResults: Flowable<ScanResult>
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