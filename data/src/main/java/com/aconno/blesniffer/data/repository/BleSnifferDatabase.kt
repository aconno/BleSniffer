package com.aconno.blesniffer.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
        entities = [DeserializerEntity::class],
        version = 11
)
abstract class BleSnifferDatabase : RoomDatabase() {

    abstract fun deserializerDao(): DeserializerDao

}