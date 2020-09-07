package com.aconno.hexinputlib.ui.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import com.aconno.hexinputlib.HexEditController
import com.aconno.hexinputlib.KeyboardManager
import com.aconno.hexinputlib.formatter.HexFormatter
import com.aconno.hexinputlib.model.HexContentModel

class HexEditText(context: Context, attributeSet: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context,attributeSet),
    IHexEditView {
    private val controller = HexEditController(this)

    init {
        showSoftInputOnFocus = false
        onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                val keyboardView = KeyboardManager.findHexKeyboardView(this)
                KeyboardManager.showHexKeyboard(keyboardView)
                keyboardView.addListener(controller)
            } else {
                val keyboardView = KeyboardManager.findHexKeyboardView(this)
                KeyboardManager.hideHexKeyboard(keyboardView)
                keyboardView.removeListener(controller)
            }
        }
        setOnClickListener { KeyboardManager.showHexKeyboard(this) }

        addTextChangedListener(HexTextWatcher())
    }

    override fun updateContent(text: String) {
        super.setText(text)
    }

    override fun getContent(): String {
        return text.toString()
    }

    fun setContent(content : String) {
        controller.loadValuesFromText(content)
    }

    fun setContent(bytes : ByteArray) {
        controller.loadValuesFromByteArray(bytes)
    }

    fun setFormatter(formatter :  HexFormatter) {
        controller.formatter = formatter
    }

    fun getContentModel() : HexContentModel {
        return controller.model
    }

    fun getValuesAsBytes() : ByteArray {
        return controller.model.getValuesAsBytes()
    }

    fun setHexValuesLimit(hexValuesLimit : Int) {
        controller.model.setValuesLimit(hexValuesLimit)
    }

    inner class HexTextWatcher : TextWatcher {
        private lateinit var contentBeforeChange : String
        private var changeStartIndex = 0
        private var charsReplaced = 0
        private var charsInserted = 0

        override fun afterTextChanged(s: Editable?) {
            if(isWholeContentReplaced()) {
                controller.onViewContentChanged()
            } else {
                val insertedChars = editableText.toString().substring(changeStartIndex,changeStartIndex + charsInserted)
                controller.onViewContentChanged(changeStartIndex,charsReplaced,insertedChars)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            contentBeforeChange = s.toString()
            changeStartIndex = start
            charsReplaced = count
            charsInserted = after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        private fun isWholeContentReplaced() = charsReplaced == contentBeforeChange.length && charsInserted == editableText.length
    }
}