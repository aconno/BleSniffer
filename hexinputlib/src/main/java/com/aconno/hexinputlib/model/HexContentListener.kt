package com.aconno.hexinputlib.model

interface HexContentListener {
    fun valueInserted(previousState : List<Char>, insertionIndex : Int, insertedValue : Int)
    fun valuesInserted(previousState: List<Char>, insertionIndex: Int, insertedValues : List<Char>)
    fun valueRemoved(previousState: List<Char>, removalIndex : Int)
    fun valuesRemoved(previousState: List<Char>, removalStartIndex : Int, removalEndIndex : Int)
}