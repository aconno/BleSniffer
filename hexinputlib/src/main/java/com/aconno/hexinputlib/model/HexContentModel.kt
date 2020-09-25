package com.aconno.hexinputlib.model

import com.aconno.hexinputlib.HexUtils
import com.aconno.hexinputlib.isHexChar
import java.lang.IllegalArgumentException
import kotlin.math.max
import kotlin.math.min

/**
 * Represents a model of some hexadecimal content. Provides methods for making changes to the content
 * and also acts as an hex content observable and therefore making it possible for [HexContentListener]
 * listeners to subscribe to content changes.
 */
class HexContentModel : HexContentObservable() {
    private val values : MutableList<Char> = mutableListOf()
    private var valuesLimit : Int = Int.MAX_VALUE

    fun setValuesLimit(limit : Int) {
        if(limit < 0) {
            throw IllegalArgumentException("Bad values limit: $limit")
        }

        this.valuesLimit = limit
    }

    /**
     * Inserts [value] to the model at index [index].
     *
     * @param index insertion index
     * @param value a hex value to insert
     */
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

    /**
     * Inserts [values] to the model at index [index].
     *
     * @param index insertion index
     * @param values hex values to insert
     */
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

    /**
     * Gets all values in the model.
     *
     * @return hex values
     */
    fun getValues() : List<Char> {
        return values.toList()
    }

    /**
     * Replaces all values in the model by the specified values.
     *
     * @param values hex values to replace the old values
     */
    fun setValues(values : List<Char>) {
        val previousState = getValues()

        this.values.clear()
        this.values.addAll(values.subList(0,min(valuesLimit,values.size)))

        notifyValuesReplaced(previousState)
    }

    /**
     * Removes value at index [index] from the model.
     *
     * @param index index of value to remove
     */
    fun removeValue(index : Int) {
        if(index < 0 || index >= values.size) {
            throw IllegalArgumentException("Index out of bounds, expected index in range [0,${values.lastIndex}], given: $index")
        }

        if(index < 0 || index > values.lastIndex) return

        val previousState = getValues()

        values.removeAt(index)

        notifyValueRemoved(previousState,index)
    }

    /**
     * Removes values in range from [startIndex] to [endIndex] (exclusive) from the model.
     *
     * @param startIndex index of the first value to remove
     * @param endIndex index of the last value to remove incremented by one
     */
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

    /**
     * Gets all values from the model as byte array, i.e. interprets each value pair as one byte.
     * For example, if there were values [F,8,B,C,3] in the model, this method would return byte
     * array [0xF8,0xBC,0x03].
     *
     * @return values from the model interpreted as array of bytes
     */
    fun getValuesAsBytes() : ByteArray {
        return HexUtils.hexToBytes(values)
    }

    /**
     * Replaces all values in the model by the specified values given as array of bytes. I.e. this method
     * converts the given bytes to list of hex values and replaces the old values in the model with them.
     *
     * @param values hex values to replace the old values, in form of byte array
     */
    fun setValues(values : ByteArray) {
        setValues(HexUtils.bytesToHex(values))
    }
}