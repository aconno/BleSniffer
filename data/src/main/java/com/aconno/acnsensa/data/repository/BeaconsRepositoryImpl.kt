package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.beacon.Beacon
import com.aconno.acnsensa.domain.beacon.BeaconsRepository
import com.aconno.acnsensa.domain.beacon.GeneralBeacon
import io.reactivex.Single

class BeaconsRepositoryImpl(
    private val beaconDao: BeaconDao
) : BeaconsRepository {
    override fun addBeacon(beacon: Beacon) {
        beaconDao.insert(toEntity(beacon))
    }

    override fun updateBeacon(beacon: Beacon) {
        beaconDao.update(toEntity(beacon))
    }

    override fun deleteBeacon(beacon: Beacon) {
        beaconDao.delete(toEntity(beacon))
    }

    override fun getAllBeacons(): Single<List<Beacon>> {
        return beaconDao.all.map { beaconEntities -> beaconEntities.map { toBeacon(it) } }
    }

    override fun getBeaconById(address: String): Single<Beacon> {
        return beaconDao.getBeaconById(address).map { beaconEntity -> toBeacon(beaconEntity) }
    }

    private fun toEntity(beacon: Beacon): BeaconEntity {
        val address = beacon.address
        val lastseen = beacon.lastseen

        return BeaconEntity(
            address = address,
            advertisementData = beacon.advertisementData,
            lastseen = lastseen,
            name = beacon.name
        )
    }

    private fun toBeacon(beaconEntity: BeaconEntity): Beacon {
        val address = beaconEntity.address
        val lastseen = beaconEntity.lastseen
        return GeneralBeacon(address, beaconEntity.advertisementData, lastseen, beaconEntity.name)
    }
}