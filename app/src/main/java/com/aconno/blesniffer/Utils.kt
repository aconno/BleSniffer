package com.aconno.blesniffer

import com.aconno.blesniffer.domain.byteformatter.ByteArrayFormatMode
import com.aconno.hexinputlib.formatter.*

fun getHexFormatterForAdvertisementBytesDisplayMode(bytesDisplayMode : ByteArrayFormatMode) : HexFormatter {
    return when(bytesDisplayMode) {
        ByteArrayFormatMode.SINGLE_BYTE_WITH_PREFIX -> PrefixedByteHexFormatter()
        ByteArrayFormatMode.PLAIN -> PlainValuesHexFormatter()
        ByteArrayFormatMode.BYTE_PAIRS -> BytePairsHexFormatter()
        ByteArrayFormatMode.SINGLE_BYTE -> SingleByteHexFormatter()
    }
}