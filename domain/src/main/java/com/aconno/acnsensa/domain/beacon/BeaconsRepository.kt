package com.aconno.acnsensa.domain.beacon

import io.reactivex.Single

/**
 * @author aconno
 */
interface BeaconsRepository {
    fun addBeacon(beacon: Beacon)
    fun updateBeacon(beacon: Beacon)
    fun deleteBeacon(beacon: Beacon)
    fun getAllBeacons(): Single<List<Beacon>>
    fun getBeaconById(address: String): Single<Beacon>
}