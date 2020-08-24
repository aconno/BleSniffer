package com.aconno.hexinputlib.ui.keyboard

interface KeyboardListener {
    fun onValueTyped(value : Char)
    fun onRemoveKeyDown()
    fun onRemoveKeyUp()
    fun onRemoveKeyLongPress()
}