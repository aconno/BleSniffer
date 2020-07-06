package com.aconno.blesniffer.domain.deserializing

import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatMode
import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatter
import com.aconno.blesniffer.domain.byteformatter.PlainFormatByteArrayFormatter
import com.aconno.blesniffer.domain.model.Device

class DeserializerFinderImpl : DeserializerFinder {

    override fun findDeserializerForDevice(deserializers : List<Deserializer>, device: Device, deviceAdvertisement : ByteArray) : Deserializer? {
        return deserializers.find {
            when (it.filterType) {
                Deserializer.Type.MAC -> device.macAddress.matches(it.pattern)
                Deserializer.Type.DATA -> deviceAdvertisementMatchesPattern(deviceAdvertisement, it.pattern)
                else -> false
            }
        }
    }

    private fun deviceAdvertisementMatchesPattern(deviceAdvertisement: ByteArray, pattern: Regex): Boolean {
        val dataHexVariousFormats = ByteArrayFormatMode.values().map { ByteArrayFormatter.getFormatter(it).formatBytes(deviceAdvertisement) }
        dataHexVariousFormats.forEach { dataHex ->
            if(dataHex.matches(pattern) or dataHex.contains(pattern)) {
                return true
            }
        }

        //if no matches, then try to match plain formats (without spaces)
        val dataHexPlainFormat = PlainFormatByteArrayFormatter().formatBytes(deviceAdvertisement)
        val patternPlainFormat = pattern.toString().replace(" ","").toRegex()

        return dataHexPlainFormat.matches(patternPlainFormat) or dataHexPlainFormat.contains(patternPlainFormat)
    }

}