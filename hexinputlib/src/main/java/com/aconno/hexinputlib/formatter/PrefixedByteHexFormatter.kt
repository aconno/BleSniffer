package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar
import java.lang.IllegalArgumentException
import java.util.*

/**
 * A hex formatter that formats values as bytes with each byte prefixed by "0x". For example, it
 * would format values [4,2,A,8,C] as "0x42 0xA8 0x0C". If there is odd number of values, then it
 * automatically inserts 0 before last hex value - for example, it would format values [4,A,C,2,F,3,7] as
 * "0x4A 0xC2 0xF3 0x07".
 */
open class PrefixedByteHexFormatter : HexFormatter {
    override fun format(values: List<Char>)  : String {
        if(values.isEmpty()) {
            return ""
        }

        val valuePairs = HexFormattersUtils.hexValuesToValuePairs(values)
        return valuePairs.joinToString(" 0x","0x")
    }

    override fun parse(text: String): List<Char> {
        val values = mutableListOf<Char>()
        val textParts =
            text.toUpperCase(Locale.ROOT).split(" ").filter { it.isNotEmpty() && it.isNotBlank() }

        for((index,part) in textParts.withIndex()) {
            values.addAll(parsePart(part,index==0,index==textParts.lastIndex))
        }

        return values
    }

    private fun parsePart(part : String, acceptIncompleteStart : Boolean, acceptIncompleteEnd : Boolean) : List<Char> {
        if(!acceptIncompleteStart && (part.first() != '0' || part.length > 1 && part[1] != 'X') ) {
            throw IncompatibleFormatException()
        }
        if(!acceptIncompleteEnd && (!part.last().isHexChar() || part.length > 1 && !part[part.lastIndex - 1].isHexChar()) ) { //checking if end is complete: the last char has to be a hex char and second to last char (if included) also has to be a hex char
            throw IncompatibleFormatException()
        }
        val prefixLength = when {
            part.startsWith("0X") -> 2
            part.startsWith("X") || part=="0" && !acceptIncompleteStart -> 1
            else -> 0
        }

        val values = part.subSequence(prefixLength,part.length)
        if(values.any { !it.isHexChar() }) {
            throw IncompatibleFormatException()
        }
        return values.toList()
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex < 0) {
            throw IllegalArgumentException("Bad formatted value index: $formattedValueIndex")
        }

        if(formattedValueIndex <= 2) {
            return 0
        }
        if(values.size == 1) {
            return if(formattedValueIndex <= 3) 0 else 1
        }

        var sourceIndex = 1
        var index = 3
        while(index < formattedValueIndex) {
            if(sourceIndex % 2 == 0) {
                index++
            } else {
                index += 4
                if(values.size % 2 != 0 && sourceIndex + 1 == values.lastIndex) { //special case: when the next value is the last value and there is odd number of values - then index should be increment by one because of pre padded 0
                    index++
                }
            }
            sourceIndex++
        }

        return sourceIndex
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        if(sourceIndex < 0 || sourceIndex > values.size) {
            throw IllegalArgumentException("Source index out of bounds, expected index in range [0,${values.size}], given: $sourceIndex")
        }

        var formattedIndex = 2 //formatted index for sourceIndex==0
        var index = 0
        while(index < sourceIndex) {
            if(index % 2 == 0 || index % 2 == 1 && index == values.lastIndex) {
                formattedIndex++
            } else {
                formattedIndex += 4
            }
            index++
        }
        if(values.size % 2 != 0 && sourceIndex >= values.lastIndex) {
            formattedIndex++
        }

        return formattedIndex
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return HexFormattersUtils.areByteValuesDerivableFrom(values,fromValues)
    }

}