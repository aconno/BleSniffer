package com.aconno.hexinputlib.formatter

class SingleByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>) : String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString(" ")
    }

    override fun parse(text: String): List<Char> {
        return HexFormattersUtils.parseGroupedHexBytes(text,1)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,1)
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