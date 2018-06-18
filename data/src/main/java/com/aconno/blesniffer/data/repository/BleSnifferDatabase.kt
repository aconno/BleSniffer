package com.aconno.blesniffer.data.repository

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase


@Database(
        entities = [DeserializerEntity::class],
        version = 11
)
abstract class BleSnifferDatabase : RoomDatabase() {

    abstract fun deserializerDao(): DeserializerDao

}