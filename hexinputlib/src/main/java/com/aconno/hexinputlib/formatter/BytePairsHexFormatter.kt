package com.aconno.hexinputlib.formatter

class BytePairsHexFormatter : HexFormatter {
    override fun format(values: List<Char>) : String {
        TODO("Not yet implemented")
    }

    override fun parse(text: String): List<Char> {
        return FormatParseUtils.parseGroupedHexBytes(text,2)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        TODO("Not yet implemented")
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        TODO("Not yet implemented")
    }
}