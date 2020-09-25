package com.aconno.hexinputlib.formatter

/**
 * A formatter used to format content of HexEditText view. It's main purpose is to translate a list of
 * hex values to a formatted string but it also contains some util methods that are required in order for
 * other components to be able to interact with the formatted content, for example to locate a source value
 * in a formatted string, to parse formatted string, etc.
 */
interface HexFormatter {
    /**
     * Formats the given list of hex values.
     *
     * @param values list of hex values to format
     *
     * @return formatted values
     *
     */
    fun format(values : List<Char>) : String

    /**
     * Parses the specified formatted content back to a list of hex values. The specified formatted
     * content can be incomplete, i.e. it can only be a substring of formatted content created by
     * formatting values using this formatter. For example, if object implementing this interface
     * formats values [5,8,F,4,3,B] to '58F 43B', then this method should accept not only '58F 43B'
     * but also any substring as value of the parameter. So, 'F 43' should be successfully parsed, i.e.
     * the method should return [F,4,3], instead of throwing an [IncompatibleFormatException]
     *
     * @param text formatted values
     *
     * @return list containing parsed hex values
     *
     * @throws IncompatibleFormatException if format of [text] is not compatible with the format
     * that this formatter produces
     */
    fun parse(text : String) : List<Char>?

    /**
     * Locates a source value from the specified list of values at which the specified formatted value
     * index is pointed at.
     *
     * For example, suppose there is a list of values [5,8,A,4,F,3] which gets formatted by an object
     * implementing this interface to '5-8 A4 F-3 '. If this method gets called with this list of values
     * as the first parameter and with a value of 3 as the second parameter, it is supposed to return 2.
     * This is because index 3 points to a space before hex value 'A' in the formatted string, so this
     * means that it is indirectly pointed at the hex value 'A'. So, that means that this method is supposed
     * to return the index of hex value 'A' in the source list of values, which is 2. If the method gets
     * called with value of 4 as the second parameter instead of the value of 3, the returned value should
     * be the same - 2. That is because index 4 is pointed directly at the hex value 'A' so the method again
     * returns index of that value. To make it absolutely clear what this method does,
     * here is a list of all (formattedValueIndex -> return value) translations for this example:
     *
     * 0 -> 0
     * 1 -> 1
     * 2 -> 1
     * 3 -> 2
     * 4 -> 2
     * 5 -> 3
     * 6 -> 4
     * 7 -> 4
     * 8 -> 5
     * 9 -> 5
     * 10 -> 6
     * 11 -> 6
     *
     * Notice that when formatted value index points at the end of values list, then it returns
     * index of the last value in the values list incremented by one. So, when formatted value index
     * is 10 or 11, the method returns 6.
     *
     * @param values list of hex values
     * @param formattedValueIndex index in range [0,N] where N is the size of the formatted string -
     * the string that has been created by formatting the [values] using this formatter
     *
     * @return index of value in [values] at which the [formattedValueIndex] is pointed at in the formatted string
     *
     */
    fun locateSourceValue(values : List<Char>, formattedValueIndex : Int) : Int

    /**
     * Locates a formatted value in a formatted string that has been created by formatting the specified
     * hex values using this formatter. The value which this method locates is specified by [sourceIndex].
     *
     * For example, suppose there is a list of values [5,8,A,4,F,3] which gets formatted by an object
     * implementing this interface to '5-8 A4 F-3 '. If this method gets called with this list of values
     * as the first parameter and with a value of 1 as the second parameter, it is supposed to return 2.
     * This is because index 1 points at hex value '8' in the values list which is placed at index 2 in
     * the formatted string. Or, if the method gets called with value of 2 as the second parameter, then
     * the method returns 4. To make it absolutely clear what this method does,
     * here is a list of all (sourceIndex -> return value) translations for this example:
     *
     * 0 -> 0
     * 1 -> 2
     * 2 -> 4
     * 3 -> 5
     * 4 -> 7
     * 5 -> 9
     * 6 -> 10/11
     *
     *
     * Notice that if the method gets called with value of 6 which is equal to size of the values
     * list incremented by one, then the method should return 10 or 11 - general rule for this case is the
     * following: the method should return an index that is larger than the index pointing at the last
     * value in the formatted string but not larger then the size of the formatted string.
     *
     * @param values list of hex values
     * @param sourceIndex index of a value in [values]
     *
     * @return index of character in the formatted string (the string that has been created by formatting
     * the [values] using this formatter) that points at the hex value specified with the method parameters
     *
     */
    fun locateFormattedValue(values : List<Char>, sourceIndex : Int) : Int

    /**
     * Checks if parsing a formatted string that has been created by formatting [fromValues] using this formatter,
     * could as a result produce [values].
     *
     * This method exists because some implementations of this interface can, when formatting a list of values,
     * automatically add some default hex values to the formatted string. For example, if there is a list of
     * values [A,B,1,3,C] and if there is a formatter that formats this values as pairs in such a way that
     * it adds 0 next to the last hex value if there is odd number of values, then formatting the values using
     * that formatter would result in 'AB 13 C0'. If, after that, this formatted string gets parsed using
     * the same formatter, it will likely as a result produce [A,B,1,3,C,0]. So, this means that calling
     * this method with [A,B,1,3,C,0] as the first parameter and with [A,B,1,3,C] as the second parameter,
     * should result in the return value of true.
     *
     * @param values list of hex values
     * @param fromValues list of hex values
     *
     * @return true if [values] can be derived by formatting [fromValues] and then parsing the formatted string
     *
     */
    fun areValuesDerivableFrom(values : List<Char>, fromValues : List<Char>) : Boolean
}