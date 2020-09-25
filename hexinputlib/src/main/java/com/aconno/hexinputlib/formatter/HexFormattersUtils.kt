package com.aconno.hexinputlib.formatter

import com.aconno.hexinputlib.isHexChar

/**
 * An object containing some util methods that could be useful for creating an implementation
 * of [HexFormatter].
 */
object HexFormattersUtils {
    private const val HEX_CHARS_PER_BYTE = 2

    /**
     * Parses [text] which is expected to be formatted as a set of byte groups of size specified by
     * the second parameter. For example, the method can be called with '58F3 CD7B' as the first
     * parameter and with 2 as the second parameter (2 bytes -> 4 hex values),
     * resulting in values [5,8,F,3,C,D,7,B]. The method can also be called with any substring compatible
     * with this byte-groups format: for example, it can be called with 'F3 CD7' as the first parameter
     * and with 2 as the second parameter and it will successfully parse giving [F,3,C,D,7] as a result,
     * because 'F3 CD7' is a substring of text formatted as byte groups of size 2.
     *
     * @param text text to be parsed
     * @param expectedGroupSizeInBytes size of group, in bytes
     *
     * @return list of parsed values
     * @throws IncompatibleFormatException if [text] is not formatted as a set of byte groups of size [expectedGroupSizeInBytes]
     */
    fun parseGroupedHexBytes(text : String, expectedGroupSizeInBytes : Int) : List<Char> {
        val expectedGroupSizeInChars = expectedGroupSizeInBytes * HEX_CHARS_PER_BYTE
        val values = mutableListOf<Char>()

        val textParts = text.split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
        for((index,part) in textParts.withIndex()) {
            if(part.length != expectedGroupSizeInChars && index > 0 && index < textParts.lastIndex || // accept incomplete group if it is the first or the last group (for example, text 'ABC 1234 567' should be accepted for expected group size of 4
                part.length > expectedGroupSizeInChars ||
                part.any { !it.isHexChar() }
            ) {

                throw IncompatibleFormatException()
            }
            part.forEach { values.add(it) }
        }

        return values
    }

    /**
     * Parses [text] that is formatted as a list of plain hex values, without any additional characters
     * or separators. For example, the method accepts '5AB65D675F6B5A' as a parameter value.
     *
     * @param text text to be parsed
     *
     * @return list of parsed hex values
     * @throws IncompatibleFormatException if [text] contains any non-hex characters, except whitespace at the beginning or at the end
     */
    fun parsePlainValues(text: String) : List<Char> {
        val trimmedText = text.trim()
        if(trimmedText.find { !it.isHexChar() } != null) {
            throw IncompatibleFormatException()
        }
        return trimmedText.toCharArray().toList()
    }

    /**
     * Groups the specified list of hex values to list of hex value pairs. For example, list of
     * values [A,2,B,8,C,D] gets grouped to [A2,B8,CD]. If there is odd number of hex values in
     * the [values] list, then a value of zero joins to the last value to make a pair - it joins to the
     * left of that last value. So, for example, list of values [A,2,B,8,C] gets grouped to [A2,B8,0C].
     *
     * @param values list of hex values
     *
     * @return list of hex value pairs created by grouping [values] into value pairs
     *
     */
    fun hexValuesToValuePairs(values : List<Char>) : List<String> {
        val pairs = mutableListOf<String>()
        for(i in values.indices step 2) {
            val pair = if(i == values.lastIndex) {
                "0${values[i]}"
            } else {
                "${values[i]}${values[i+1]}"
            }
            pairs.add(pair)
        }

        return pairs
    }

    /**
     * Locates a source value from the specified list of values at which the specified formatted value
     * index is pointed at. The method is to be used only for [values] that are formatted using
     * some byte-groups formatter, i.e. a formatter that groups values as groups of bytes (specifically,
     * this method accepts formats where group size is [groupSizeInBytes]) - see description of
     * [parseGroupedHexBytes] if it is not clear enough what byte-groups format is like.
     *
     * This method is actually a util method that can be used for implementation of [HexFormatter.locateSourceValue]
     * method. For example, if there is a formatter that formats values as groups of 2 bytes, implementation
     * of [HexFormatter.locateSourceValue] for that formatter can be a one-liner that calls this method
     * with value of 2 as third parameter ([groupSizeInBytes]).
     *
     * @param values list of hex values
     * @param formattedValueIndex index in range [0,N] where N is the size of the formatted string -
     * the string that has been created by formatting the [values] using byte-groups formatter that
     * formats values as byte groups of size [groupSizeInBytes]
     * @param groupSizeInBytes size of byte groups in bytes
     *
     * @return index of value in [values] at which the [formattedValueIndex] is pointed at in the formatted string
     *
     */
    fun locateSourceValueInGroupedHexBytesString(values: List<Char>, formattedValueIndex: Int, groupSizeInBytes : Int) : Int {
        val spacesBeforeTargetValue = formattedValueIndex/(groupSizeInBytes * HEX_CHARS_PER_BYTE + 1) // +1 for space between groups
        var index = formattedValueIndex - spacesBeforeTargetValue
        if(values.size % 2 == 1 && index > values.lastIndex) { // special case: formattedValueIndex is next to last value and there is odd number of values, so the pre padded 0 in last byte should be ignored because it is not part of values list
            index--
        }

        return index
    }

    /**
     * Locates a formatted value in a formatted string that has been created by formatting the specified
     * hex values using byte-groups formatter that formats values as byte groups of size [groupSizeInBytes]
     * (see description of [parseGroupedHexBytes] if it is not clear enough what byte-groups format is like).
     * The value which this method locates is specified by [sourceIndex].
     *
     * This method is actually a util method that can be used for implementation of [HexFormatter.locateFormattedValue]
     * method. For example, if there is a formatter that formats values as groups of 2 bytes, implementation
     * of [HexFormatter.locateFormattedValue] for that formatter can be a one-liner that calls this method
     * with value of 2 as third parameter ([groupSizeInBytes]).
     *
     * @param values list of hex values
     * @param sourceIndex index of value in [values] that is to be located in a formatted string
     * @param groupSizeInBytes size of byte groups in bytes
     *
     * @return index of character in the formatted string (the string that has been created by formatting
     * the [values] using byte-groups formatter that formats values as byte groups of size [groupSizeInBytes])
     * that points at the hex value specified with the method parameters
     *
     */
    fun locateFormattedValueInGroupedHexBytesString(values: List<Char>, sourceIndex: Int, groupSizeInBytes : Int) : Int {
        val groupSizeInChars = groupSizeInBytes * HEX_CHARS_PER_BYTE
        var index = sourceIndex + sourceIndex/groupSizeInChars
        if(values.size % 2 == 1 && sourceIndex >= values.lastIndex) {
            index++
        } else if(values.size % groupSizeInChars == 0 && sourceIndex == values.size) {
            index--
        }
        return index
    }

    /**
     * Checks if parsing a formatted string that has been created by formatting [fromValues] using some
     * bytes formatter, could as a result produce [values]. Bytes formatter is any formatter that
     * interprets [values] as bytes (each value pair treats as one byte) and automatically inserts
     * zero before last hex value if there is odd number of values (so that the last value does not
     * stand alone in the formatted string).
     *
     * This method is actually a util method that can be used for implementation of [HexFormatter.areValuesDerivableFrom]
     * method. For example, if there is a formatter that groups values as byte groups of size N,
     * implementation of [HexFormatter.areValuesDerivableFrom] can be a one-liner that calls this
     * method.
     *
     * @param byteValues list of hex values
     * @param fromValues list of hex values
     *
     * @return true if [values] can be derived by formatting [fromValues] using some byte-groups
     * formatter and then parsing the formatted string
     */
    fun areByteValuesDerivableFrom(byteValues: List<Char>, fromValues : List<Char>) : Boolean {
        if(fromValues == byteValues) {
            return true
        }
        if(byteValues.size - fromValues.size == 1) {
            val sizeWithoutLastByte = byteValues.size - 2
            if(byteValues.last() == fromValues.last() && byteValues[byteValues.lastIndex - 1] == '0' && byteValues.subList(0,sizeWithoutLastByte) == fromValues.subList(0,sizeWithoutLastByte)) {
                return true
            }
        }
        return false
    }
}