package com.aconno.blesniffer.domain.byteformatter

enum class ByteArrayFormatMode {
        SINGLE_BYTE_WITH_PREFIX, // 0x01 0x23 0x45
        SINGLE_BYTE, // 01 23 45
        BYTE_PAIRS, // 0123 4567
        PLAIN // 0123456789ABCDEF
    }