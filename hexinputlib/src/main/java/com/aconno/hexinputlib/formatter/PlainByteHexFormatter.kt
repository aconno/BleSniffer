package com.aconno.hexinputlib.formatter

import java.lang.IllegalArgumentException

/**
 * A hex formatter that formats values as one large group of bytes. Since it interprets given hex values
 * as bytes (i.e. interprets each value pair as one byte), it inserts 0 before last hex value if there
 * is odd number of values. For example, it would format values [8,3,1,B,4] as "831B04".
 */
open class PlainByteHexFormatter : HexFormatter {

    override fun format(values: List<Char>): String {
        return HexFormattersUtils.hexValuesToValuePairs(values).joinToString("")
    }

    override fun parse(text: String): List<Char>? {
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
        if(sourceIndex < 0 || sourceIndex > values.size) {
            throw IllegalArgumentException("Source index out of bounds, expected index in range [0,${values.size}], given: $sourceIndex")
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