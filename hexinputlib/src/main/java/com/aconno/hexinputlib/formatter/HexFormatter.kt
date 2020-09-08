package com.aconno.hexinputlib.formatter

interface HexFormatter {
    fun format(values : List<Char>) : String
    fun parse(text : String) : List<Char>
    fun locateSourceValue(values : List<Char>, formattedValueIndex : Int) : Int
    fun locateFormattedValue(values : List<Char>, sourceIndex : Int) : Int
    fun areValuesDerivableFrom(values : List<Char>, fromValues : List<Char>) : Boolean
}