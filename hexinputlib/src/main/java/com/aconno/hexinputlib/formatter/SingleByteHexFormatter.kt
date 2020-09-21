package com.aconno.hexinputlib.formatter

class SingleByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>) : String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString(" ")
    }

    override fun parse(text: String): List<Char>? {
        return HexFormattersUtils.parseGroupedHexBytes(text,1)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,1)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        return HexFormattersUtils.locateFormattedValueInGroupedHexBytesString(values,sourceIndex,1)
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }
}