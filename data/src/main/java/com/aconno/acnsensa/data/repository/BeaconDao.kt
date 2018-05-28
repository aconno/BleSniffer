package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.*
import io.reactivex.Single

/**
 * @author aconno
 */
@Dao
abstract class BeaconDao {

    @get:Query("SELECT * FROM beacons")
    abstract val all: Single<List<BeaconEntity>>

    @Query("SELECT * FROM beacons WHERE address = :address")
    abstract fun getBeaconById(address: String): Single<BeaconEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(beacon: BeaconEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun update(beacon: BeaconEntity)

    @Delete
    abstract fun delete(beacon: BeaconEntity)
}