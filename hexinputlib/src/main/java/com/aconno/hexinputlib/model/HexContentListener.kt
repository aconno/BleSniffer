package com.aconno.hexinputlib.model

/**
 * A listener that listens to [HexContentModel] changes.
 */
interface HexContentListener {
    /**
     * Called when a value [insertedValue] has been inserted into hex content model at index [insertionIndex].
     *
     * @param previousState previous state of hex content model
     * @param insertionIndex insertion index
     * @param insertedValue value that has been inserted
     *
     */
    fun valueInserted(previousState : List<Char>, insertionIndex : Int, insertedValue : Char)

    /**
     * Called when multiple values [insertedValues] have been inserted into hex content model at index
     *  [insertionIndex].
     *
     *  @param previousState previous state of hex content model
     *  @param insertionIndex insertion index
     *  @param insertedValues values that have been inserted
     */
    fun valuesInserted(previousState: List<Char>, insertionIndex: Int, insertedValues : List<Char>)

    /**
     * Called when a value at index [removalIndex] of model previous state [previousState] has been removed.
     *
     * @param previousState previous state of hex content model
     * @param removalIndex removal index
     *
     */
    fun valueRemoved(previousState: List<Char>, removalIndex : Int)

    /**
     * Called when a range of values starting at index [removalEndIndex] and ending at index [removalEndIndex] (exclusive)
     * has been removed from model previous state [previousState].
     *
     * @param previousState previous state of hex content model
     * @param removalStartIndex index of the first value that has been removed
     * @param removalEndIndex index of the last value that has been removed incremented by one
     */
    fun valuesRemoved(previousState: List<Char>, removalStartIndex : Int, removalEndIndex : Int)

    /**
     * Called when all values from model previous state [previousState] have been replaced by new values.
     *
     * @param previousState previous state of hex content model
     */
    fun valuesReplaced(previousState: List<Char>)
}