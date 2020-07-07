package com.aconno.blesniffer.domain.byteformatter

import java.lang.StringBuilder

class BytePairsByteArrayFormatter : ByteArrayFormatter {

    override fun formatBytes(byteArray: ByteArray): String {

        val builder = StringBuilder()
        for(i in 0..byteArray.lastIndex step 2) {
            val bytePair = byteArray.slice(IntRange(i, (i + 1).coerceAtMost(byteArray.lastIndex)))
                .joinToString(separator = "") {
                    it.toInt().and(0xff).toString(16).padStart(
                    2,
                    '0'
                    ).toUpperCase()
                }
            builder.append(bytePair)
            builder.append(" ")
        }

        return builder.toString()
    }

}