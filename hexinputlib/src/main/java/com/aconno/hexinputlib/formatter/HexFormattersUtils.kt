package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

object HexFormattersUtils {
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

    fun hexValuesToValuePairs(values : List<Char>) : List<String> {
        val pairs = mutableListOf<String>()
        for(i in values.indices step 2) {
            val pair = if(i == values.lastIndex) {
                "0${values[i]}"
            } else {
                "${values[i]}${values[i+1]}"
            }
            pairs.add(pair)
        }

        return pairs
    }

    fun locateSourceValueInGroupedHexBytesString(values: List<Char>, formattedValueIndex: Int, groupSizeInBytes : Int) : Int {
        val spacesBeforeTargetValue = formattedValueIndex/(groupSizeInBytes * HEX_CHARS_PER_BYTE + 1) // +1 for space between groups
        var index = formattedValueIndex - spacesBeforeTargetValue
        if(values.size % 2 == 1 && index > values.lastIndex) { // special case: formattedValueIndex is next to last value and there is odd number of values, so the pre padded 0 in last byte should be ignored because it is not part of values list
            index--
        }

        return index
    }

}