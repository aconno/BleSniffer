package com.aconno.hexinputlib.ui.editor

interface IHexEditView {
    fun updateContent(text : String)
    fun getContent() : String
    fun setSelection(start : Int, end : Int)
    fun getSelectionStart() : Int
    fun getSelectionEnd() : Int
}