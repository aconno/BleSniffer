package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

/**
 * A hex formatter that formats values as bytes separated by a single whitespace. For example,
 * it would format values [4,3,B,2,C,D] as "43 B2 CD". Since it interprets given hex values
 * as bytes, it inserts 0 before last hex value if there is odd number of values. So, for example,
 * it would format values [4,3,B,2,C] as "43 B2 0C".
 */
open class SingleByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>) : String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString(" ")
    }

    override fun parse(text: String): List<Char>? {
        return HexFormattersUtils.parseGroupedHexBytes(text,1)
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        return HexFormattersUtils.locateSourceValueInGroupedHexBytesString(values,formattedValueIndex,1)
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0 || sourceIndex > values.size) {
            throw IllegalArgumentException("Source index out of bounds, expected index in range [0,${values.size}], given: $sourceIndex")
        }

        return HexFormattersUtils.locateFormattedValueInGroupedHexBytesString(values,sourceIndex,1)
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }
}