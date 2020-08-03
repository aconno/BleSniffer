package com.aconno.hexinputlib.formatter

import java.lang.StringBuilder

class SingleByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>) : String {
        val builder = StringBuilder()
        for(i in values.indices step 2) {
            if(i == values.lastIndex) {
                builder.append("0")
                builder.append(values[i])
            } else {
                builder.append(values[i])
                builder.append(values[i+1])
            }
            builder.append(" ")
        }

        return builder.trim().toString()
    }

    override fun parse(text: String): List<Char> {
        return FormatParseUtils.parseGroupedHexBytes(text,1)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        var index = formattedValueIndex - formattedValueIndex/3
        if(values.size % 2 == 1 && index > values.lastIndex) { // special case: formattedValueIndex is next to last value and there is odd number of values, so the pre padded 0 in last byte should be ignored because it is not part of values list
            index--
        }

        return index
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        var index = sourceIndex + sourceIndex/2
        if(values.size % 2 == 1 && sourceIndex >= values.lastIndex) {
            index++
        } else if(sourceIndex == values.size) {
            index--
        }
        return index
    }
}