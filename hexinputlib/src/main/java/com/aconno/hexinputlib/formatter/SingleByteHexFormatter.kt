package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

open class SingleByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>) : String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString(" ")
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parseGroupedHexBytes(text,1)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,1)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0) {
            throw IllegalArgumentException("Bad source index: $sourceIndex")
        }

        return HexFormattersUtils.locateFormattedValueInGroupedHexBytesString(values,sourceIndex,1)
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }
}