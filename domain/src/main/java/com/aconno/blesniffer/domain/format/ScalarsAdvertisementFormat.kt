package com.aconno.blesniffer.domain.format

/**
 * @author aconno
 */
class ScalarsAdvertisementFormat : BleSnifferBaseFormat() {

    private val requiredFormat: List<Byte> = generateBleSnifferRequiredFormat(0x01)

    override fun getFormat(): Map<String, ByteFormat> {
        val baseFormat: Map<String, ByteFormat> = super.getFormat()

        val temperature = Pair(
            TEMPERATURE,
            ByteFormat(
                startIndexInclusive = 10,
                endIndexExclusive = 14,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val humidity = Pair(
            HUMIDITY,
            ByteFormat(
                startIndexInclusive = 14,
                endIndexExclusive = 18,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val pressure = Pair(
            PRESSURE,
            ByteFormat(
                startIndexInclusive = 18,
                endIndexExclusive = 22,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )
        val light = Pair(
            LIGHT,
            ByteFormat(
                startIndexInclusive = 22,
                endIndexExclusive = 26,
                isReversed = true,
                targetType = SupportedTypes.FLOAT
            )
        )

        val batteryLevel = Pair(
            BATTERY_LEVEL,
            ByteFormat(
                startIndexInclusive = 26,
                endIndexExclusive = 27,
                isReversed = false,
                targetType = SupportedTypes.BYTE
            )
        )

        return baseFormat + mapOf(temperature, humidity, pressure, light, batteryLevel)
    }

    override fun getRequiredFormat(): List<Byte> = requiredFormat

    override fun getMaskBytePositions(): List<Int> = bleSnifferMaskBytesPosition

    companion object {
        const val TEMPERATURE = "Temperature"
        const val HUMIDITY = "Humidity"
        const val PRESSURE = "Pressure"
        const val LIGHT = "Light"
        const val BATTERY_LEVEL = "Battery Level"
    }
}