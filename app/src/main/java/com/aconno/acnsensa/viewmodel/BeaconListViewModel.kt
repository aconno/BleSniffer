package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.beacon.Beacon
import io.reactivex.Flowable

class BeaconListViewModel(
    private val beaconData: Flowable<Beacon>
) : ViewModel() {
    private val beaconLiveData: MutableLiveData<Beacon> = MutableLiveData()

    init {
        subscribe()
    }

    private fun subscribe() {
        beaconData.subscribe { processBeaconData(it) }
    }

    private fun processBeaconData(data: Beacon) {
        beaconLiveData.value = data
    }

    fun getBeaconLiveData(): MutableLiveData<Beacon> {
        return beaconLiveData
    }
}