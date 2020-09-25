package com.aconno.hexinputlib.model

import com.aconno.hexinputlib.HexUtils

/**
 * Represents a model of some hexadecimal content. Provides methods for making changes to the content
 * and also acts as an hex content observable and therefore making it possible for [HexContentListener]
 * listeners to subscribe to content changes.
 */
class HexContentModel : HexContentObservable() {
    private val values : MutableList<Char> = mutableListOf()

    /**
     * Inserts [value] to the model at index [index].
     *
     * @param index insertion index
     * @param value a hex value to insert
     */
    fun insertValue(index : Int, value : Char) {
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
        val previousState = getValues()

        this.values.addAll(index,values)

        notifyValuesInserted(previousState,index,values)
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
        this.values.addAll(values)

        notifyValuesReplaced(previousState)
    }

    /**
     * Removes value at index [index] from the model.
     *
     * @param index index of value to remove
     */
    fun removeValue(index : Int) {
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