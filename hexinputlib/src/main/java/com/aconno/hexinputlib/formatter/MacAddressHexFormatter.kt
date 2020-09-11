package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar
import java.lang.StringBuilder

open class MacAddressHexFormatter : HexFormatter {

    override fun format(values: List<Char>)  : String {
        return StringBuilder().apply {
            values.forEachIndexed { index, c ->
                if(index > 0 && index%2 == 0) {
                    append(":")
                }
                append(c)
            }
        }.toString()
    }

    override fun parse(text: String): List<Char> {
        if(!isFormatCompatible(text)) {
            throw IncompatibleFormatException()
        }
        return HexFormattersUtils.parseGroupedHexBytes(text.replace(":"," "),1) //replacing all ':' with space gives text in format of grouped hex bytes with group size of 1 byte
    }

    private fun isFormatCompatible(text: String) : Boolean {
        val parts = text.trim().split(":")
        parts.forEachIndexed { index, part ->
            if(part.isEmpty() && index > 0 && index < parts.lastIndex) { //empty parts are allowed only in start or in the text end
                return false
            }
            if(part.any { !it.isHexChar() }) {
                return false
            }
        }

        return true
    }

    override fun locateSourceValue(values: List<Char>, formattedValueIndex: Int): Int {
        return formattedValueIndex - formattedValueIndex/3
    }

    override fun locateFormattedValue(values: List<Char>, sourceIndex: Int): Int {
        var index = sourceIndex + sourceIndex/2
        if(values.size % 2 == 0 && sourceIndex == values.size) {
            index--
        }
        return index
    }

    override fun areValuesDerivableFrom(values: List<Char>, fromValues: List<Char>): Boolean {
        return values == fromValues
    }
}