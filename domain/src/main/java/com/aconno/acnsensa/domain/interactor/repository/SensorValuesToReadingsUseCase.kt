package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.format.ScalarsAdvertisementFormat
import com.aconno.acnsensa.domain.format.VectorsAdvertisementFormat
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.*
import io.reactivex.Single

/**
 * @aconno
 */
class SensorValuesToReadingsUseCase :
    SingleUseCaseWithParameter<List<Reading>, Map<String, Number>> {
    override fun execute(parameter: Map<String, Number>): Single<List<Reading>> {
        val timestamp: Long = System.currentTimeMillis()

        val readings: List<Reading?> = listOf(
            makeAccelerometerReading(timestamp, parameter),
            makeGyroscopeReading(timestamp, parameter),
            makeHumidityReading(timestamp, parameter),
            makeLightReading(timestamp, parameter),
            makeMagnetometerReading(timestamp, parameter),
            makePressureReading(timestamp, parameter),
            makeTemperatureReading(timestamp, parameter),
            makeBatteryLevelReading(timestamp, parameter)
        )

        return Single.just(readings.filterNotNull())
    }

    private fun makeAccelerometerReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val x = values[VectorsAdvertisementFormat.ACCELEROMETER_X]
        val y = values[VectorsAdvertisementFormat.ACCELEROMETER_Y]
        val z = values[VectorsAdvertisementFormat.ACCELEROMETER_Z]

        return if (x != null && y != null && z != null) {
            AccelerometerReading(timestamp, x, y, z)
        } else {
            null
        }
    }

    private fun makeGyroscopeReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val x = values[VectorsAdvertisementFormat.GYROSCOPE_X]
        val y = values[VectorsAdvertisementFormat.GYROSCOPE_Y]
        val z = values[VectorsAdvertisementFormat.GYROSCOPE_Z]

        return if (x != null && y != null && z != null) {
            GyroscopeReading(timestamp, x, y, z)
        } else {
            null
        }
    }

    private fun makeHumidityReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val humidity = values[ScalarsAdvertisementFormat.HUMIDITY]
        humidity?.let { return HumidityReading(timestamp, it) }
        return null
    }

    private fun makeLightReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val light = values[ScalarsAdvertisementFormat.LIGHT]
        light?.let { return LightReading(timestamp, it) }
        return null
    }

    private fun makeMagnetometerReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val x = values[VectorsAdvertisementFormat.MAGNETOMETER_X]
        val y = values[VectorsAdvertisementFormat.MAGNETOMETER_Y]
        val z = values[VectorsAdvertisementFormat.MAGNETOMETER_Z]

        return if (x != null && y != null && z != null) {
            MagnetometerReading(timestamp, x, y, z)
        } else {
            null
        }
    }

    private fun makePressureReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val pressure = values[ScalarsAdvertisementFormat.PRESSURE]
        pressure?.let { return PressureReading(timestamp, it) }
        return null
    }

    private fun makeTemperatureReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val temperature = values[ScalarsAdvertisementFormat.TEMPERATURE]
        temperature?.let { return TemperatureReading(timestamp, it) }
        return null
    }

    private fun makeBatteryLevelReading(timestamp: Long, values: Map<String, Number>): Reading? {
        val batteryLevel = values[ScalarsAdvertisementFormat.BATTERY_LEVEL]
        batteryLevel?.let { return BatteryReading(timestamp, it) }
        return null
    }
}