package com.aconno.hexinputlib

import com.aconno.hexinputlib.formatter.HexFormatters
import com.aconno.hexinputlib.formatter.IncompatibleFormatException
import com.aconno.hexinputlib.model.HexContentListener
import com.aconno.hexinputlib.model.HexContentModel
import com.aconno.hexinputlib.ui.editor.IHexEditView
import com.aconno.hexinputlib.ui.keyboard.KeyboardListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

internal class HexEditController(private val view : IHexEditView) : HexContentListener,
    KeyboardListener {
    var model : HexContentModel = HexContentModel()
    var formatter = HexFormatters.getDefaultFormatter()
        set(value) {
            field = value
            loadValuesFromText(view.getContent()) //reloading text that is currently in view so that it gets reformatted using new formatter
        }

    private var sequentialValueRemovalDisposable : Disposable? = null

    init {
        model.addListener(this)
    }

    fun loadValuesFromText(textValues : String) {
        val values = try {
             HexFormatters.parse(textValues)
        } catch (ex : IncompatibleFormatException) {
            listOf<Char>()
        }

        model.setValues(values)
    }

    fun onViewContentChanged() {
        val content = view.getContent()
        val parsedValues = formatter.parse(view.getContent())

        if(parsedValues == null) {
            loadValuesFromText(content)
        } else if(!formatter.areValuesDerivableFrom(parsedValues,model.getValues())) {
            model.setValues(parsedValues)
        }
    }

    fun onViewContentChanged(changeStartIndex : Int, charsReplacedCount : Int, charsInserted : String) {
        removeTextPart(changeStartIndex, changeStartIndex + charsReplacedCount)

        val valuesInserted = insertValuesFromText(charsInserted,changeStartIndex)
        if(!valuesInserted) {
            view.updateContent(formatter.format(model.getValues()))
            view.setSelection(changeStartIndex,changeStartIndex)
        }
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
        val newCursorIndex = if(values.isNotEmpty()) {
            formatter.locateFormattedValue(values, removalStartIndex)
        } else 0

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
            removeValueBeforeCursor()
        }
    }

    private fun removeValueBeforeCursor() {
        val removalIndex = formatter.locateSourceValue(model.getValues(),view.getSelectionStart()) - 1
        model.removeValue(removalIndex)
    }

    override fun onRemoveKeyLongPress() {
        sequentialValueRemovalDisposable = Observable.interval(TIME_BETWEEN_AUTO_VALUE_REMOVAL,TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                removeValueBeforeCursor()
            }
    }

    override fun onRemoveKeyUp() {
        sequentialValueRemovalDisposable?.dispose()
    }

    override fun onValueTyped(value: Char) {
        val insertionIndex = formatter.locateSourceValue(model.getValues(),view.getSelectionStart())

        removeSelectedText()

        model.insertValue(insertionIndex,value)
    }

    private fun removeSelectedText() {
        return removeTextPart(view.getSelectionStart(),view.getSelectionEnd())
    }

    private fun removeTextPart(removalStartIndex : Int, removalEndIndex : Int) {
        val values = model.getValues()

        val removalStartSourceIndex = formatter.locateSourceValue(values,removalStartIndex)
        val removalEndSourceIndex =
            if(removalEndIndex == removalStartIndex) removalStartSourceIndex
            else formatter.locateSourceValue(values,removalEndIndex)

        model.removeRange(removalStartSourceIndex,removalEndSourceIndex)
    }

    fun loadValuesFromByteArray(bytes: ByteArray) {
        model.setValues(bytes)
    }

    private fun insertValuesFromText(text: String, insertAtIndex : Int) : Boolean {
        val insertionIndex = formatter.locateSourceValue(model.getValues(),insertAtIndex)

        removeSelectedText()

        val values = try {
            HexFormatters.parse(text.toUpperCase(Locale.ROOT))
        } catch (ex : IncompatibleFormatException) {
            return false
        }

        model.insertValues(insertionIndex,values)

        return true
    }

    companion object {
        const val TIME_BETWEEN_AUTO_VALUE_REMOVAL = 100L
    }
}