package com.aconno.acnsensa.domain.interactor.beacon

import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.beacon.BeaconsRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

/**
 * @author aconno
 */
class GetAllBeaconsUseCase(
    private val beaconsRepository: BeaconsRepository
) : SingleUseCase<List<Beacon>> {
    override fun execute(): Single<List<Beacon>> {
        return beaconsRepository.getAllBeacons()
    }
}