package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

class PrefixedByteHexFormatter : HexFormatter {
    override fun format(values: List<Char>)  : String {
        val valuePairs = HexFormattersUtils.hexValuesToValuePairs(values)
        return valuePairs.joinToString(" 0x","0x")
    }

    override fun parse(text: String): List<Char> {
        val values = mutableListOf<Char>()
        val textParts = text.split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
        for(part in textParts) {
            if(part.length != EXPECTED_FORMAT_PART_SIZE || !part.startsWith("0x") ||
                !part[FORMAT_PART_FIRST_CHAR_INDEX].isHexChar() ||
                !part[FORMAT_PART_SECOND_CHAR_INDEX].isHexChar()) {

                throw IncompatibleFormatException()
            }

            values.add(part[FORMAT_PART_FIRST_CHAR_INDEX])
            values.add(part[FORMAT_PART_SECOND_CHAR_INDEX])
        }

        return values
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex <= 2) return 0
        var sourceIndex = 1
        var index = 3
        while(index < formattedValueIndex) {
            if(sourceIndex % 2 == 0) {
                index++
            } else {
                index += 4
                if(values.size % 2 != 0 && sourceIndex + 1 == values.lastIndex) { //special case: when the next value is the last value and there is odd number of values - then index should be increment by one because of pre padded 0
                    index++
                }
            }
            sourceIndex++
        }

        return sourceIndex
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        var formattedIndex = 2 //formatted index for sourceIndex==0
        var index = 0
        while(index < sourceIndex) {
            if(index % 2 == 0 || index % 2 == 1 && index == values.lastIndex) {
                formattedIndex++
            } else {
                formattedIndex += 4
            }
            index++
        }
        if(values.size % 2 != 0 && sourceIndex >= values.lastIndex) {
            formattedIndex++
        }

        return formattedIndex
    }

    companion object {
        const val EXPECTED_FORMAT_PART_SIZE = 4
        const val FORMAT_PART_FIRST_CHAR_INDEX = 2
        const val FORMAT_PART_SECOND_CHAR_INDEX = 3
    }
}