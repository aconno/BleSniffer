package com.aconno.hexinputlib.model

import com.aconno.hexinputlib.HexUtils
import com.aconno.hexinputlib.isHexChar
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

class HexContentModel : HexContentObservable() {
    private val values : MutableList<Char> = mutableListOf()
    private var valuesLimit : Int = Int.MAX_VALUE

    fun setValuesLimit(limit : Int) {
        if(limit < 0) {
            throw IllegalArgumentException("Bad values limit: $limit")
        }

        this.valuesLimit = limit
    }

    fun insertValue(index : Int, value : Char) {
        if(index < 0 || index >= values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.lastIndex}], given: $index")
        }
        if(!value.isHexChar()) {
            throw IllegalArgumentException("Bad value: $value")
        }

        if(values.size == valuesLimit) return

        val previousState = getValues()

        values.add(index,value)

        notifyValueInserted(previousState,index,value)
    }

    fun insertValues(index : Int, values : List<Char>) {
        if(index < 0 || index >= values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.lastIndex}], given: $index")
        }

        val previousState = getValues()

        val valuesExceedingLimit = max(0,this.values.size + values.size - valuesLimit) // making sure that the values limit doesn't get surpassed by excluding last N values that would cause the limit get surpassed
        val valuesToInsert = values.subList(0,values.size - valuesExceedingLimit)
        this.values.addAll(index,valuesToInsert)

        notifyValuesInserted(previousState,index,valuesToInsert)
    }

    fun getValues() : List<Char> {
        return values.toList()
    }

    fun setValues(values : List<Char>) {
        val previousState = getValues()

        this.values.clear()
        this.values.addAll(values.subList(0,min(valuesLimit,values.size)))

        notifyValuesReplaced(previousState)
    }

    fun removeValue(index : Int) {
        if(index < 0 || index >= values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.lastIndex}], given: $index")
        }

        if(index < 0 || index > values.lastIndex) return

        val previousState = getValues()

        values.removeAt(index)

        notifyValueRemoved(previousState,index)
    }

    fun removeRange(startIndex : Int, endIndex : Int) {
        if(startIndex < 0 || startIndex > values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.size}], given: $startIndex")
        }
        if(endIndex < 0 || endIndex > values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.size}], given: $endIndex")
        }

        if(startIndex == endIndex) return

        val previousState = getValues()

        values.subList(startIndex,endIndex).clear()

        notifyValuesRemoved(previousState,startIndex,endIndex)
    }

    fun getValuesAsBytes() : ByteArray {
        return HexUtils.hexToBytes(values)
    }

    fun setValues(values : ByteArray) {
        setValues(HexUtils.bytesToHex(values))
    }
}