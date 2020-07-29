package com.aconno.hexinputlib

import com.aconno.hexinputlib.formatter.HexFormatter
import com.aconno.hexinputlib.model.HexContentListener
import com.aconno.hexinputlib.model.HexContentModel
import com.aconno.hexinputlib.ui.editor.IHexEditView
import com.aconno.hexinputlib.ui.keyboard.KeyboardListener

class HexEditController(private val view : IHexEditView) : HexContentListener,
    KeyboardListener {
    lateinit var model : HexContentModel
    lateinit var formatter : HexFormatter

    init {
        TODO()
    }

    override fun valueInserted(previousState: List<Char>, insertionIndex: Int, insertedValue: Int) {
        TODO("Not yet implemented")
    }

    override fun valueRemoved(previousState: List<Char>, removalIndex: Int) {
        TODO("Not yet implemented")
    }

    override fun valuesInserted(
        previousState: List<Char>,
        insertionIndex: Int,
        insertedValues: List<Char>
    ) {
        TODO("Not yet implemented")
    }

    override fun valuesRemoved(
        previousState: List<Char>,
        removalStartIndex: Int,
        removalEndIndex: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onRemoveKeyDown() {
        TODO("Not yet implemented")
    }

    override fun onRemoveKeyLongPress() {
        TODO("Not yet implemented")
    }

    override fun onRemoveKeyUp() {
        TODO("Not yet implemented")
    }

    override fun onValueTyped(value: Char) {
        TODO("Not yet implemented")
    }
}