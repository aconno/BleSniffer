package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

open class PlainByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString("")
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parsePlainValues(text)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        if(values.size % 2 == 1 && formattedValueIndex > values.lastIndex) {
            return formattedValueIndex - 1
        }
        return formattedValueIndex
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0) {
            throw IllegalArgumentException("Bad source index: $sourceIndex")
        }

        if(values.size % 2 == 1 && sourceIndex >= values.lastIndex) {
            return sourceIndex + 1
        }
        return sourceIndex
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }

}