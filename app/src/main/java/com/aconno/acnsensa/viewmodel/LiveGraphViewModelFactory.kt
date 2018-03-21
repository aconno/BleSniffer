package com.aconno.acnsensa.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.aconno.acnsensa.domain.interactor.bluetooth.GetReadingsUseCase
import io.reactivex.Flowable

/**
 * @author aconno
 */
class LiveGraphViewModelFactory(
    private val sensorValues: Flowable<Map<String, Number>>,
    private val getReadingsUseCase: GetReadingsUseCase
) : ViewModelProvider.Factory {

    @SuppressWarnings("UNCHECKED_CAST") //Safe to suppress since as? casting is being used.
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel: T? =
            LiveGraphViewModel(sensorValues, getReadingsUseCase) as? T
        viewModel?.let { return viewModel }

        throw IllegalArgumentException("Illegal cast for LiveGraphViewModelFactory")
    }
}