package com.aconno.hexinputlib.ui.editor

import android.content.Context
import android.util.AttributeSet
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


}