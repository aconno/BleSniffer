package com.aconno.hexinputlib.model

class HexContentModel : HexContentObservable() {
    private val values : MutableList<Char> = mutableListOf()

    fun insertValue(index : Int, value : Char) {
        val previousState = getValues()

        values.add(index,value)

        notifyValueInserted(previousState,index,value)
    }

    fun insertValues(index : Int, values : List<Char>) {
        val previousState = getValues()

        this.values.addAll(index,values)

        notifyValuesInserted(previousState,index,values)
    }

    fun getValues() : List<Char> {
        return values.toList()
    }

    fun removeValue(index : Int) {
        val previousState = getValues()

        values.removeAt(index)

        notifyValueRemoved(previousState,index)
    }

    fun removeRange(startIndex : Int, endIndex : Int) {
        val previousState = getValues()

        values.subList(startIndex,endIndex).clear()

        notifyValuesRemoved(previousState,startIndex,endIndex)
    }

    fun getValuesAsBytes() : ByteArray {
        return HexConverter.hexToBytes(values)
    }
}