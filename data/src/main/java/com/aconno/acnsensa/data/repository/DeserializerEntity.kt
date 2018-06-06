package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "deserializers")
data class DeserializerEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val filter: String,
        val filterType: String,
        val fieldDeserializers: String
)