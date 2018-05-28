package com.aconno.acnsensa.domain.interactor.beacon

import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.beacon.BeaconsRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

/**
 * @author aconno
 */
class InsertBeaconUseCase(
    private val beaconsRepository: BeaconsRepository
) : SingleUseCaseWithParameter<Boolean, Beacon> {
    override fun execute(parameter: Beacon): Single<Boolean> {
        beaconsRepository.addBeacon(parameter)
        return Single.just(true)
    }
}