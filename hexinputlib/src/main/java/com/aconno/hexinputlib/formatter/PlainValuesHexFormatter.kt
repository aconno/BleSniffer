package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

open class PlainValuesHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return values.joinToString("")
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parsePlainValues(text)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0) {
            throw IllegalArgumentException("Bad source index: $sourceIndex")
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