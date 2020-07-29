package com.aconno.hexinputlib.ui.keyboard

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class BaseHexKeyboardView(context : Context, attributeSet: AttributeSet) : FrameLayout(context,attributeSet) {
    private var listeners = mutableListOf<KeyboardListener>()

    fun addListener(keyboardListener: KeyboardListener) {
        TODO()
    }

    fun removeListener(keyboardListener: KeyboardListener) {
        TODO()
    }

    protected fun notifyValueTyped(value : Char) {
        TODO()
    }

    protected fun notifyRemoveKeyDown() {
        TODO()
    }

    protected fun notifyRemoveKeyLongPress() {
        TODO()
    }

    protected fun notifyRemoveKeyUp() {
        TODO()
    }
}