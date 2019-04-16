package com.aconno.blesniffer.data.remote.mappers

import android.graphics.Color
import com.aconno.blesniffer.data.remote.model.BeaconFormat
import com.aconno.blesniffer.data.remote.model.BeaconSettingsSupport
import com.aconno.blesniffer.data.remote.model.ByteFormat
import com.aconno.blesniffer.data.remote.model.ByteFormatRequired
import com.aconno.blesniffer.domain.ValueConverter
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.FieldDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer
import timber.log.Timber
import java.util.*

class DeserializerMapper {
    fun map(beaconFormat: BeaconFormat): Deserializer {
        return GeneralDeserializer(
            id = beaconFormat.id.toLong(16),
            name = beaconFormat.name,
            filterType = Deserializer.Type.DATA,
            fieldDeserializers = getFieldDeserializers(beaconFormat.dataFormats),
            filter = getFilter(beaconFormat)
        )
    }

    private fun getFieldDeserializers(formatsRequired: List<ByteFormat>): MutableList<FieldDeserializer> {
        return formatsRequired.map {
            getFieldDeserializer(it)
        }.toMutableList()
    }

    private fun getFieldDeserializer(byteFormat: ByteFormat): FieldDeserializer {
        val name = byteFormat.name
        val type = getFieldDeserializerType(byteFormat.dataType) ?: ValueConverter.BOOLEAN
        val startIndex = byteFormat.startIndexInclusive + BYTE_SWITCH
        val endIndex = byteFormat.endIndexExclusive + BYTE_SWITCH


        return GeneralFieldDeserializer(
            name = name,
            type = type,
            startIndexInclusive = startIndex,
            endIndexExclusive = endIndex,
            color = generateRandomColor()
        )
    }

    private fun generateRandomColor(): Int {
        val random = Random()
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    private fun getFieldDeserializerType(dataType: String): ValueConverter? {
        return ValueConverter.values().find { it.name.equals(dataType, ignoreCase = true) }
    }

    private fun getFilter(
        beaconFormat: BeaconFormat
    ): String {

        val filterBuilder = StringBuilder()

        val formatsList =
            getFormatList(beaconFormat.formatsRequired, beaconFormat.settingsSupport)


        filterBuilder.append(ADVERTISEMENT_DATA_TYPE)

        filterBuilder.append(formatsList.joinToString(separator = " "))

        val dataCount = getDataByteCount(formatsList.size, beaconFormat.dataFormats)

        filterBuilder.insert(0, dataCount)

        val filter = filterBuilder.toString()
        Timber.d(filter)
        return filter
    }

    private fun getFormatList(
        deviceFormatList: List<ByteFormatRequired>,
        beaconSettingsSupport: BeaconSettingsSupport?
    ): List<ByteFormatRequired> {
        val formatList = mutableListOf<ByteFormatRequired>()
        formatList.addAll(deviceFormatList)

        beaconSettingsSupport?.let {
            formatList[it.index] = ByteFormatRequired(it.index, it.mask)
        }

        return formatList.sorted()
    }

    private fun getDataByteCount(currentByteCount: Int, dataFormats: List<ByteFormat>): String {
        //The byte describing the advertisement data type must be counted
        var byteCount = currentByteCount + 1

        dataFormats.forEach {
            byteCount += (it.endIndexExclusive - it.startIndexInclusive)
        }

        val countHexa = if (byteCount < 16) "0${byteCount.toString(16)}"
        else byteCount.toString(16)

        return "0x${countHexa.toUpperCase()} "
    }

    companion object {
        private const val ADVERTISEMENT_DATA_TYPE = "0xFF "
        /**
         * Number of bytes to add to the format indexes, beacuse the format doesn't count neither
         * the length nor the advertisement data type bytes.
         */
        private const val BYTE_SWITCH = 2
    }
}
