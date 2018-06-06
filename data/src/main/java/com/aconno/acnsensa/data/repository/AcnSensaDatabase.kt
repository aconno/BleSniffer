package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * @author aconno
 */
@Database(entities = [ActionEntity::class, BeaconEntity::class, DeserializerEntity::class], version = 6)
abstract class AcnSensaDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun beaconDao(): BeaconDao
    abstract fun deserializerDao(): DeserializerDao
}