package com.aconno.hexinputlib.formatter

import java.lang.StringBuilder
import kotlin.math.min

class BytePairsHexFormatter : HexFormatter {
    override fun format(values: List<Char>) : String {
        val valuePairs = HexFormattersUtils.hexValuesToValuePairs(values)

        val builder = StringBuilder()
        for(i in valuePairs.indices step 2) {
            val bytePair = valuePairs.subList(i,min(i+2,valuePairs.size)).joinToString("")
            builder.append(bytePair)
            builder.append(" ")
        }

        return builder.trim().toString()
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parseGroupedHexBytes(text,2)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,2)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        return HexFormattersUtils.locateFormattedValueInGroupedHexBytesString(values,sourceIndex,2)
    }
}