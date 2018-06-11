package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.aconno.acnsensa.data.repository.devices.DeviceDao
import com.aconno.acnsensa.data.repository.devices.DeviceEntity


@Database(
        entities = [ActionEntity::class, BeaconEntity::class, DeserializerEntity::class, DeviceEntity::class, GooglePublishEntity::class, RESTPublishEntity::class, GooglePublishDeviceJoinEntity::class, RestPublishDeviceJoinEntity::class],
        version = 8
)
abstract class AcnSensaDatabase : RoomDatabase() {

    abstract fun publishDeviceJoinDao(): PublishDeviceJoinDao

    abstract fun deviceDao(): DeviceDao

    abstract fun actionDao(): ActionDao

    abstract fun beaconDao(): BeaconDao

    abstract fun deserializerDao(): DeserializerDao

    abstract fun googlePublishDao(): GooglePublishDao

    abstract fun restPublishDao(): RESTPublishDao
}