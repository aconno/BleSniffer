package com.aconno.blesniffer.domain.byteformatter

class PlainFormatByteArrayFormatter : ByteArrayFormatter {
    override fun formatBytes(byteArray: ByteArray): String {
        return byteArray.joinToString(separator = "") {
            it.toInt().and(0xff).toString(16).padStart(
                2,
                '0'
            ).toUpperCase()
        }
    }


}