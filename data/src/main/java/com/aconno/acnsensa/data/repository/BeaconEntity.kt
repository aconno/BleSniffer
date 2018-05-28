package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "beacons")
data class BeaconEntity(
    @PrimaryKey var address: String,
    var advertisementData: ByteArray,
    var lastseen: Long,
    val name: String
)