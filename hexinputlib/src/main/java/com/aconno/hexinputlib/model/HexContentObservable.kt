package com.aconno.hexinputlib.model

open class HexContentObservable {
    private val listeners = mutableListOf<HexContentListener>()

    fun addListener(hexContentListener: HexContentListener) {
        listeners.add(hexContentListener)
    }

    fun removeListener(hexContentListener: HexContentListener) {
        listeners.remove(hexContentListener)
    }

    protected fun notifyValueInserted(previousState: List<Char>, insertionIndex: Int, insertedValue: Char) {
        listeners.forEach { it.valueInserted(previousState,insertionIndex,insertedValue) }
    }

    protected fun notifyValuesInserted(previousState: List<Char>, insertionIndex: Int, insertedValues : List<Char>) {
        listeners.forEach { it.valuesInserted(previousState,insertionIndex,insertedValues) }
    }

    protected fun notifyValueRemoved(previousState: List<Char>, removalIndex : Int) {
        listeners.forEach { it.valueRemoved(previousState,removalIndex) }
    }

    protected fun notifyValuesRemoved(previousState: List<Char>, removalStartIndex : Int, removalEndIndex : Int) {
        listeners.forEach { it.valuesRemoved(previousState,removalStartIndex,removalEndIndex) }
    }
}