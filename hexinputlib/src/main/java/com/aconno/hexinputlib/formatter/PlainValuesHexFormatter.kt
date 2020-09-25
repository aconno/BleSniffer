package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

/**
 * A hex formatter that formats values without adding any extra characters or doing any value grouping,
 * i.e. it just concatenates all hex values given. For example, it would format values [5,A,3,C,9]
 * as "5A3C9".
 */
open class PlainValuesHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return values.joinToString("")
    }

    override fun parse(text: String): List<Char>? {
        return HexFormattersUtils.parsePlainValues(text)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0 || sourceIndex > values.size) {
            throw IllegalArgumentException("Source index out of bounds, expected index in range [0,${values.size}], given: $sourceIndex")
        }

        return sourceIndex
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        return formattedValueIndex
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return values == fromValues
    }
}