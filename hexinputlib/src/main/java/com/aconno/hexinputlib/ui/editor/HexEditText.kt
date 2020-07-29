package com.aconno.hexinputlib.ui.editor

import android.content.Context
import android.util.AttributeSet
import com.aconno.hexinputlib.HexEditController
import com.aconno.hexinputlib.KeyboardManager
import com.aconno.hexinputlib.model.HexContentModel

class HexEditText(context: Context, attributeSet: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context,attributeSet),
    IHexEditView {
    private val controller = HexEditController(this)

    init {
        showSoftInputOnFocus = false
        onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                KeyboardManager.showHexKeyboard(this@HexEditText)
            } else {
                KeyboardManager.hideHexKeyboard(this@HexEditText)
            }
        }
        setOnClickListener { KeyboardManager.showHexKeyboard(this) }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val keyboardView = KeyboardManager.findHexKeyboardView(this)
        keyboardView.addListener(controller)
    }

    override fun updateContent(text: String) {
        super.setText(text)
    }

    override fun getContent(): String {
        return text.toString()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        controller.loadValuesFromText(text.toString())
    }

    fun getContentModel() : HexContentModel {
        return controller.model
    }

    fun getValuesAsBytes() : ByteArray {
        return controller.model.getValuesAsBytes()
    }


}