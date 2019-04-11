package com.aconno.blesniffer.data.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deserializers")
data class DeserializerEntity(
        @PrimaryKey(autoGenerate = true) var id: Long? = null,
        val name: String,
        val filter: String,
        val filterType: String,
        val fieldDeserializers: String,
        @Suppress("ArrayInDataClass") @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val sampleData: ByteArray
)