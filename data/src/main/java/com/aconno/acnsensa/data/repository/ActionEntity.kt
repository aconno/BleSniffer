package com.aconno.acnsensa.data.repository

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "actions")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String,
    var sensorType: Int,
    var conditionType: Int,
    var value: Float,
    var outcomeMessage: String,
    var outcomeType: Int,
    var destination: String
)