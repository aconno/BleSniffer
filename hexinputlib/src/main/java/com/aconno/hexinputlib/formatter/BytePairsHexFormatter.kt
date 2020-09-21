package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.math.min

open class BytePairsHexFormatter : HexFormatter {
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
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,2)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0 || sourceIndex > values.size) {
            throw IllegalArgumentException("Source index out of bounds, expected index in range [0,${values.size}], given: $sourceIndex")
        }

        return HexFormattersUtils.locateFormattedValueInGroupedHexBytesString(values,sourceIndex,2)
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }
}