package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(
        entities = [DeserializerEntity::class],
        version = 9
)
abstract class AcnSensaDatabase : RoomDatabase() {

    abstract fun deserializerDao(): DeserializerDao

}