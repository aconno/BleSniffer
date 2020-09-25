package com.aconno.hexinputlib.formatter

/**
 * A hex formatter that formats values without adding any extra characters or doing any value grouping,
 * i.e. it just concatenates all hex values given. For example, it would format values [5,A,3,C,9]
 * as "5A3C9".
 */
class PlainValuesHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return values.joinToString("")
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parsePlainValues(text)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        return sourceIndex
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        return formattedValueIndex
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return values == fromValues
    }
}