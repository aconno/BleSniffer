package com.aconno.hexinputlib.ui.keyboard

/**
 * A listener that listens to [BaseHexKeyboardView] events.
 */
interface KeyboardListener {
    /**
     * Called when the specified value has been typed on the keyboard.
     *
     * @param value a value that has been typed
     */
    fun onValueTyped(value : Char)

    /**
     * Called when remove key gets pressed.
     */
    fun onRemoveKeyDown()

    /**
     * Called when remove key gets released.
     */
    fun onRemoveKeyUp()

    /**
     * Called when remove key is long pressed. This method should not be called more than once between
     * remove key up and down events. For example, if remove key gets pressed for 10 seconds, this
     * method should be called once after, for example, one second (or after some other time interval
     *  - this can be defined arbitrarily) and should not be called again even though it is still
     * being pressed for 9 seconds.
     */
    fun onRemoveKeyLongPress()
}