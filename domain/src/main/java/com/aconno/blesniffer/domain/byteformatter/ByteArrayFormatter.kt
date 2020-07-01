package com.aconno.blesniffer.domain.byteformatter

interface ByteArrayFormatter {
    fun formatBytes(byteArray: ByteArray) : String

    companion object {
        fun getFormatter(formatMode: ByteArrayFormatMode) : ByteArrayFormatter {
            return when(formatMode) {
                ByteArrayFormatMode.SINGLE_BYTE_WITH_PREFIX -> SingleByteWithPrefixByteArrayFormatter()
                ByteArrayFormatMode.SINGLE_BYTE -> SingleByteByteArrayFormatter()
                ByteArrayFormatMode.BYTE_PAIRS -> BytePairsByteArrayFormatter()
            }
        }
    }

}