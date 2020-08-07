package com.aconno.hexinputlib

import com.aconno.hexinputlib.formatter.HexFormatters
import com.aconno.hexinputlib.formatter.IncompatibleFormatException
import com.aconno.hexinputlib.model.HexContentListener
import com.aconno.hexinputlib.model.HexContentModel
import com.aconno.hexinputlib.ui.editor.IHexEditView
import com.aconno.hexinputlib.ui.keyboard.KeyboardListener
import java.text.ParseException

class HexEditController(private val view : IHexEditView) : HexContentListener,
    KeyboardListener {
    var model : HexContentModel = HexContentModel()
    var formatter = HexFormatters.getDefaultFormatter()
        set(value) {
            field = value
            loadValuesFromText(view.getContent()) //reloading text that is currently in view so that it gets reformatted using new formatter
        }

    init {
        model.addListener(this)
    }

    fun loadValuesFromText(textValues : String) {
        val values = try {
             HexFormatters.parse(textValues)
        } catch (ex : IncompatibleFormatException) {
            return
        }

        model.setValues(values)
    }

    override fun valueInserted(previousState: List<Char>, insertionIndex: Int, insertedValue: Char) {
        valuesInserted(previousState,insertionIndex, listOf(insertedValue))
    }

    override fun valueRemoved(previousState: List<Char>, removalIndex: Int) {
        valuesRemoved(previousState,removalIndex,removalIndex + 1)
    }

    override fun valuesInserted(
        previousState: List<Char>,
        insertionIndex: Int,
        insertedValues: List<Char>
    ) {
        val values = model.getValues()
        val newCursorIndex = formatter.locateFormattedValue(values,insertionIndex + insertedValues.size)

        view.updateContent(formatter.format(values))
        view.setSelection(newCursorIndex,newCursorIndex)
    }

    override fun valuesRemoved(
        previousState: List<Char>,
        removalStartIndex: Int,
        removalEndIndex: Int
    ) {
        val values = model.getValues()
        val newCursorIndex = formatter.locateFormattedValue(values,removalStartIndex)

        view.updateContent(formatter.format(values))
        view.setSelection(newCursorIndex,newCursorIndex)
    }

    override fun valuesReplaced(previousState: List<Char>) {
        val newContent = formatter.format(model.getValues())
        view.updateContent(newContent)
        view.setSelection(newContent.length, newContent.length)
    }

    override fun onRemoveKeyDown() {
        if(view.getSelectionStart() != view.getSelectionEnd()) {
            removeSelectedText()
        } else {
            val removalIndex = formatter.locateSourceValue(model.getValues(),view.getSelectionStart()) - 1
            model.removeValue(removalIndex)
        }
    }

    override fun onValueTyped(value: Char) {
        val insertionIndex = formatter.locateSourceValue(model.getValues(),view.getSelectionStart())

        removeSelectedText()

        model.insertValue(insertionIndex,value)
    }

    private fun removeSelectedText() {
        val values = model.getValues()

        val selectionStartSourceIndex = formatter.locateSourceValue(values,view.getSelectionStart())
        val selectionEndSourceIndex =
            if(view.getSelectionEnd() == view.getSelectionStart()) selectionStartSourceIndex
            else formatter.locateSourceValue(values,view.getSelectionEnd())

        model.removeRange(selectionStartSourceIndex,selectionEndSourceIndex)
    }

    fun loadValuesFromByteArray(bytes: ByteArray) {
        model.setValues(bytes)
    }

    fun cutSelection() {
        removeSelectedText()
    }

    fun paste(clipboardText: String) {
        val insertionIndex = formatter.locateSourceValue(model.getValues(),view.getSelectionStart())

        removeSelectedText()

        val values = try {
            HexFormatters.parse(clipboardText)
        } catch (ex : IncompatibleFormatException) {
            return
        }

        model.insertValues(insertionIndex,values)
    }
}