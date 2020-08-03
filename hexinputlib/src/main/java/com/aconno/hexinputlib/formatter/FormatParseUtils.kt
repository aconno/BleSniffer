package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

object FormatParseUtils {
    private const val HEX_CHARS_PER_BYTE = 2

    fun parseGroupedHexBytes(text : String, expectedGroupSizeInBytes : Int) : List<Char> {
        val expectedGroupSizeInChars = expectedGroupSizeInBytes * HEX_CHARS_PER_BYTE
        val values = mutableListOf<Char>()

        val textParts = text.split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
        for(part in textParts) {
            if(part.length != expectedGroupSizeInChars || part.filter { it.isHexChar() }.length != expectedGroupSizeInChars) {
                throw IncompatibleFormatException()
            }
            part.forEach { values.add(it) }
        }

        return values
    }

}