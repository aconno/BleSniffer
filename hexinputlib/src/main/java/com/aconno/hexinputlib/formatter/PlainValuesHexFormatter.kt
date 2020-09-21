package com.aconno.hexinputlib.formatter

class PlainValuesHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return values.joinToString("")
    }

    override fun parse(text: String): List<Char>? {
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