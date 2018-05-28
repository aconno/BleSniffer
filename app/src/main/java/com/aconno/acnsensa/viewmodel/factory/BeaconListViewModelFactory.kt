package com.aconno.acnsensa.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.viewmodel.BeaconListViewModel
import io.reactivex.Flowable

/**
 * @author aconno
 */
class BeaconListViewModelFactory(
    private val beaconData: Flowable<Beacon>
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = BeaconListViewModel(beaconData)
        return getViewModel(viewModel, modelClass)
    }
}