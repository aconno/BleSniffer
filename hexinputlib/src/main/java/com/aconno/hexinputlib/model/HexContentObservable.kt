package com.aconno.hexinputlib.model

import com.aconno.hexinputlib.model.HexContentListener

open class HexContentObservable {
    private val values = mutableListOf<Char>()
    private val listeners = mutableListOf<HexContentListener>()

    fun addListener(hexContentListener: HexContentListener) {
        TODO()
    }

    fun removeListener(hexContentListener: HexContentListener) {
        TODO()
    }

    protected fun notifyValueInserted(previousState : List<Char>, insertionIndex : Int, insertedValue : Int) {
        TODO()
    }

    protected fun notifyValuesInserted(previousState: List<Char>, insertionIndex: Int, insertedValues : List<Char>) {
        TODO()
    }

    protected fun notifyValueRemoved(previousState: List<Char>, removalIndex : Int) {
        TODO()
    }

    protected fun notifyValuesRemoved(previousState: List<Char>, removalStartIndex : Int, removalEndIndex : Int) {
        TODO()
    }
}