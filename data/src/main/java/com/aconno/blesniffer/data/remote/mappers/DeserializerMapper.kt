package com.aconno.blesniffer.data.remote.mappers

import com.aconno.blesniffer.data.remote.model.BeaconFormat
import com.aconno.blesniffer.data.remote.model.ByteFormat
import com.aconno.blesniffer.data.remote.model.ByteFormatRequired
import com.aconno.blesniffer.domain.ValueConverter
import com.aconno.blesniffer.domain.deserializing.Deserializer
import com.aconno.blesniffer.domain.deserializing.FieldDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralDeserializer
import com.aconno.blesniffer.domain.deserializing.GeneralFieldDeserializer

class DeserializerMapper {
    fun map(beaconFormat: BeaconFormat): Deserializer {
        return GeneralDeserializer(
            id = beaconFormat.id.toLong(16),
            name = beaconFormat.name,
            filterType = Deserializer.Type.DATA,
            fieldDeserializers = getFieldDeserializers(beaconFormat.dataFormats),
            filter = getFilter(beaconFormat.formatsRequired)
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
        val startIndex: Int
        val endIndex: Int
        if (byteFormat.reversed) {
            startIndex = byteFormat.endIndexExclusive - 1
            endIndex = byteFormat.startIndexInclusive - 1
        } else {
            startIndex = byteFormat.startIndexInclusive
            endIndex = byteFormat.endIndexExclusive
        }

        return GeneralFieldDeserializer(
            name = name,
            type = type,
            startIndexInclusive = startIndex,
            endIndexExclusive = endIndex
        )
    }

    private fun getFieldDeserializerType(dataType: String): ValueConverter? {
        return ValueConverter.values().find { it.name.equals(dataType, ignoreCase = true) }
    }

    private fun getFilter(deviceFormat: List<ByteFormatRequired>): String {
        val result = StringBuilder()
        deviceFormat.forEachIndexed { index, byteFormatRequired ->
            if (index == byteFormatRequired.index) {
                result.append(byteFormatRequired.value).append(" ")
            }
        }
        return result.toString().trim()
    }
}
