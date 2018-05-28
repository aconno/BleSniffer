package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Entity

@Entity(tableName = "deserializers", primaryKeys = ["filter", "filterType"])
data class DeserializerEntity(
    val filter: String,
    val filterType: String,
    val valueDeserializers: String
)