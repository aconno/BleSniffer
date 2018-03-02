package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(sensorType: SensorType): List<Reading>
}