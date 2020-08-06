package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

object HexFormattersUtils {
    private const val HEX_CHARS_PER_BYTE = 2

    fun parseGroupedHexBytes(text : String, expectedGroupSizeInBytes : Int) : List<Char> {
        val expectedGroupSizeInChars = expectedGroupSizeInBytes * HEX_CHARS_PER_BYTE
        val values = mutableListOf<Char>()

        val textParts = text.split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
        for((index,part) in textParts.withIndex()) {
            if(part.length != expectedGroupSizeInChars && index > 0 && index < textParts.lastIndex || // accept incomplete group if it is the first or the last group (for example, text 'ABC 1234 567' should be accepted for expected group size of 4
                part.length > expectedGroupSizeInChars ||
                part.any { !it.isHexChar() }
            ) {

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

    fun locateFormattedValueInGroupedHexBytesString(values: List<Char>, sourceIndex: Int, groupSizeInBytes : Int) : Int {
        val groupSizeInChars = groupSizeInBytes * HEX_CHARS_PER_BYTE
        var index = sourceIndex + sourceIndex/groupSizeInChars
        if(values.size % 2 == 1 && sourceIndex >= values.lastIndex) {
            index++
        } else if(values.size % groupSizeInChars == 0 && sourceIndex == values.size) {
            index--
        }
        return index
    }

}