package com.aconno.hexinputlib.ui.keyboard

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

abstract class BaseHexKeyboardView(context : Context, attributeSet: AttributeSet) : FrameLayout(context,attributeSet) {
    private var listeners = mutableListOf<KeyboardListener>()

    fun addListener(keyboardListener: KeyboardListener) {
        listeners.add(keyboardListener)
    }

    fun removeListener(keyboardListener: KeyboardListener) {
        listeners.remove(keyboardListener)
    }

    protected fun notifyValueTyped(value : Char) {
        listeners.forEach { it.onValueTyped(value) }
    }

    protected fun notifyRemoveKeyDown() {
        listeners.forEach { it.onRemoveKeyDown() }
    }

}