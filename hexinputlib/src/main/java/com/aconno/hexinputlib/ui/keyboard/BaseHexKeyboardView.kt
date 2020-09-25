package com.aconno.hexinputlib.ui.keyboard

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * A base class that represents a keyboard specialised for input of hexadecimal content. It provides
 * methods for handling [KeyboardListener] listeners. Responsibility of a child class is to call an appropriate
 * method to notify the listeners about a keyboard event, e.g. a value typed event.
 *
 */
abstract class BaseHexKeyboardView(context : Context, attributeSet: AttributeSet?) : FrameLayout(context,attributeSet) {
    private var listeners = mutableListOf<KeyboardListener>()

    /**
     * Adds the specified listener.
     *
     * @param keyboardListener a keyboard listener to add
     */
    fun addListener(keyboardListener: KeyboardListener) {
        listeners.add(keyboardListener)
    }

    /**
     * Removes the specified listener.
     *
     * @param keyboardListener a keyboard listener to remove
     */
    fun removeListener(keyboardListener: KeyboardListener) {
        listeners.remove(keyboardListener)
    }

    /**
     * Notifies listeners that the specified value has been typed on the keyboard.
     *
     * @param value a value that has been typed
     */
    protected fun notifyValueTyped(value : Char) {
        listeners.forEach { it.onValueTyped(value) }
    }

    /**
     * Notifies listeners that remove key has got pressed.
     */
    protected fun notifyRemoveKeyDown() {
        listeners.forEach { it.onRemoveKeyDown() }
    }

    /**
     * Notifies listeners that remove key is long pressed.
     */
    protected fun notifyRemoveKeyLongPress() {
        listeners.forEach { it.onRemoveKeyLongPress() }
    }

    /**
     * Notifies listeners that remove key has got released.
     */
    protected fun notifyRemoveKeyUp() {
        listeners.forEach { it.onRemoveKeyUp() }
    }
}