package com.aconno.hexinputlib.model

class HexContentModel : HexContentObservable() {
    private val values = mutableListOf<Char>()

    fun insertValue(index : Int, value : Char) {
        TODO()
    }

    fun insertValues(index : Int, values : List<Char>) {
        TODO()
    }

    fun getValuesAsBytes() : ByteArray {
        TODO()
    }

    fun getValues() : List<Char> {
        TODO()
    }

    fun removeValue(index : Int) {
        TODO()
    }

    fun removeRange(startIndex : Int, endIndex : Int) {
        TODO()
    }

}