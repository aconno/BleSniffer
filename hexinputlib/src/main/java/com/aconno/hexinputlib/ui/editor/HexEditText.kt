package com.aconno.hexinputlib.ui.editor

import android.content.Context
import android.util.AttributeSet
import com.aconno.hexinputlib.model.HexContentModel

class HexEditText(context: Context, attributeSet: AttributeSet) : androidx.appcompat.widget.AppCompatEditText(context,attributeSet),
    IHexEditView {
    override fun updateContent(text: String) {
        TODO("Not yet implemented")
    }

    override fun getContent(): String {
        TODO("Not yet implemented")
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        TODO()
    }

    fun getContentModel() : HexContentModel {
        TODO()
    }

    fun getValuesAsBytes() : ByteArray {
        TODO()
    }


}