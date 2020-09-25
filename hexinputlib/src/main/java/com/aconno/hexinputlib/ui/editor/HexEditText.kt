package com.aconno.hexinputlib.ui.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.core.widget.addTextChangedListener
import com.aconno.hexinputlib.HexEditController
import com.aconno.hexinputlib.KeyboardManager
import com.aconno.hexinputlib.R
import com.aconno.hexinputlib.formatter.HexFormatter
import com.aconno.hexinputlib.formatter.HexFormatters
import com.aconno.hexinputlib.model.HexContentModel

/**
 * A special type of EditText view intended for input of hexadecimal content. It automatically formats
 * it's content using the specified formatter or a default one. It also provides method that enables
 * setting content as formatted string, that gets automatically parsed and formatted using the specified
 * formatter, and as array of bytes. What's more, it is connected to a hexadecimal keyboard that is specialised
 * for input of hex content (see [HexKeyboardView][com.aconno.hexinputlib.ui.keyboard.HexKeyboardView]).
 * But, to be able to use this view, there has to be a [HexKeyboardView][com.aconno.hexinputlib.ui.keyboard.HexKeyboardView]
 * added into the content view of an activity that uses this view. It can be placed anywhere in the content
 * view, it will be automatically found and used when this view gets focus.
 */
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

        applyAttributes(attributeSet)
    }

    private fun applyAttributes(attributeSet: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attributeSet, R.styleable.HexEditText,0,0)

        val hexValuesLimit = typedArray.getInt(R.styleable.HexEditText_hexValuesLimit,Int.MAX_VALUE)
        setHexValuesLimit(hexValuesLimit)

        val formatterNumber = typedArray.getInt(R.styleable.HexEditText_formatter,-1)
        val formatter = if(formatterNumber < 0) {
            HexFormatters.getDefaultFormatter()
        } else {
            val formatterType = HexFormatters.FormatterType.values()[formatterNumber]
            HexFormatters.getFormatter(formatterType)
        }

        setFormatter(formatter)
    }

    override fun updateContent(text: String) {
        super.setText(text)
    }

    override fun getContent(): String {
        return text.toString()
    }

    /**
     * Sets the specified content as the content of this view. The [content] can be given in any of
     * the formats that this library supports by default (see classes implementing [HexFormatter] interface
     * to find out which formats are supported, each of the formats that these classes produce are supported).
     * The provided content will be automatically formatted using a formatter that has been specified
     * using [setFormatter] method or using a default formatter.
     *
     * @param content content to be set as the content of this view
     */
    fun setContent(content : String) {
        controller.loadValuesFromText(content)
    }

    /**
     * Sets the specified bytes as the content of this view. The [bytes] get converted to their hex
     * representation which gets set as the content of this view.
     *
     * @param bytes bytes to be set as the content of this view
     */
    fun setContent(bytes : ByteArray) {
        controller.loadValuesFromByteArray(bytes)
    }

    /**
     * Sets formatter that is to be used to format this view's content.
     *
     * @param formatter a hex formatter to be used to format this view's content.
     */
    fun setFormatter(formatter :  HexFormatter) {
        controller.formatter = formatter
    }

    /**
     * Gets this view's content model. This enables direct manipulation over content of this view
     * which is useful when there is a need to only insert or remove some values from the content
     * instead of completely replacing the old content.
     *
     * @return this view's content model
     */
    fun getContentModel() : HexContentModel {
        return controller.model
    }

    /**
     * Gets the view's content interpreted as array of bytes (each value pair is interpreted as one byte).
     * For example, if the content of this view was "F8 BC A3" in the model, this method would return byte
     * array [0xF8,0xBC,0xA3].
     *
     * @return the view's content interpreted as array of bytes
     */
    fun getValuesAsBytes() : ByteArray {
        return controller.model.getValuesAsBytes()
    }

    fun setHexValuesLimit(hexValuesLimit : Int) {
        controller.model.setValuesLimit(hexValuesLimit)
    }

    fun doAfterTextChanges(afterTextChanges : ((String) -> Unit)) {
        addTextChangedListener(afterTextChanged = {afterTextChanges(getContent())})
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