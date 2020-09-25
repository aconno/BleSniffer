package com.aconno.hexinputlib.model

/**
 * Represents a hex content holder providing methods for [HexContentListener] listeners to subscribe
 * in order to receive notifications when the hex content changes.
 */
open class HexContentObservable {
    private val listeners = mutableListOf<HexContentListener>()

    /**
     * Adds the specified listener.
     *
     * @param hexContentListener listener to add
     */
    fun addListener(hexContentListener: HexContentListener) {
        listeners.add(hexContentListener)
    }

    /**
     * Removes the specified listener.
     *
     * @param hexContentListener listener to remove
     */
    fun removeListener(hexContentListener: HexContentListener) {
        listeners.remove(hexContentListener)
    }

    /**
     * Notifies listeners that value [insertedValue] has been inserted into the hex content at index [insertionIndex].
     *
     * @param previousState previous state of the hex content
     * @param insertionIndex insertion index
     * @param insertedValue value that has been inserted
     */
    protected fun notifyValueInserted(previousState: List<Char>, insertionIndex: Int, insertedValue: Char) {
        listeners.forEach { it.valueInserted(previousState,insertionIndex,insertedValue) }
    }

    /**
     * Notifies listeners that values [insertedValues] have been inserted into the hex content at index
     *  [insertionIndex].
     *
     *  @param previousState previous state of the hex content
     *  @param insertionIndex insertion index
     *  @param insertedValues values that have been inserted
     */
    protected fun notifyValuesInserted(previousState: List<Char>, insertionIndex: Int, insertedValues : List<Char>) {
        listeners.forEach { it.valuesInserted(previousState,insertionIndex,insertedValues) }
    }

    /**
     * Notifies listeners that a value at index [removalIndex] of the hex content previous state [previousState] has been removed.
     *
     * @param previousState previous state of the hex content
     * @param removalIndex removal index
     */
    protected fun notifyValueRemoved(previousState: List<Char>, removalIndex : Int) {
        listeners.forEach { it.valueRemoved(previousState,removalIndex) }
    }

    /**
     * Notifies listeners that a range of values starting at index [removalStartIndex] and ending at index [removalEndIndex] (exclusive)
     * has been removed from the hex content previous state [previousState].
     *
     * @param previousState previous state of the hex content
     * @param removalStartIndex index of the first value that has been removed
     * @param removalEndIndex index of the last value that has been removed incremented by one
     */
    protected fun notifyValuesRemoved(previousState: List<Char>, removalStartIndex : Int, removalEndIndex : Int) {
        listeners.forEach { it.valuesRemoved(previousState,removalStartIndex,removalEndIndex) }
    }

    /**
     * Notifies listeners that all values from the hex content previous state [previousState] have been replaced by new values.
     *
     * @param previousState previous state of hex content model
     */
    protected fun notifyValuesReplaced(previousState: List<Char>) {
        listeners.forEach { it.valuesReplaced(previousState) }
    }
}