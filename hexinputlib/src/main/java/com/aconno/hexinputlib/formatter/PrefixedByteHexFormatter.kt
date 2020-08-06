package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

class PrefixedByteHexFormatter : HexFormatter {
    override fun format(values: List<Char>)  : String {
        val valuePairs = HexFormattersUtils.hexValuesToValuePairs(values)
        return valuePairs.joinToString(" 0x","0x")
    }

    override fun parse(text: String): List<Char> {
        val values = mutableListOf<Char>()
        val textParts = text.split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
        for((index,part) in textParts.withIndex()) {
            val partValues =
                if(part.length == 4) {
                    parseCompletePart(part)
                } else {
                    when (index) {
                        0 -> {
                            parsePartWithIncompleteStart(part) //if this is the first part of text, then it should be accepted if it has incomplete start, for example 'xFF','FF' and 'F' should be accepted as the first part of text
                        }
                        textParts.lastIndex -> {
                            parsePartWithIncompleteEnd(part) //if this is the last part of text, then it should be accepted if it has incomplete end, for example '0xF','0x' and '0' should be accepted as the last part of text
                        }
                        else -> {
                            throw IncompatibleFormatException()
                        }
                    }
                }

            values.addAll(partValues)
        }

        return values
    }

    private fun parsePartWithIncompleteStart(part : String) : List<Char> {
        return if(part.length == 2 || part.length == 3 && part.startsWith("x")) {
            parsePartWithoutPrefixChecks(part)
        } else if(part.length == 1 && part[0].isHexChar()) {
            listOf(part[0])
        } else {
            throw IncompatibleFormatException()
        }

    }

    private fun parsePartWithIncompleteEnd(part : String) : List<Char> {
        return if(part.length == 3 && part.startsWith("0x") && part[2].isHexChar()) {
            listOf(part[2])
        } else if(part.length == 2 && part == "0x" || part.length == 1 && part == "0") {
            listOf()
        } else {
            throw IncompatibleFormatException()
        }

    }

    private fun parseCompletePart(part : String) : List<Char> {
        return if(part.startsWith("0x")) {
            parsePartWithoutPrefixChecks(part)
        } else {
            throw IncompatibleFormatException()
        }

    }

    private fun parsePartWithoutPrefixChecks(part : String) : List<Char> {
        val values = part.subSequence(part.length - 2, part.length)
        if(values.any { !it.isHexChar() }) {
            throw IncompatibleFormatException()
        }
        return values.toList()
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        if(formattedValueIndex <= 2) return 0
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

}