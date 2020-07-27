package com.aconno.hexinputlib

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.Button
import android.widget.FrameLayout
import com.aconno.blesniffer.R

class HexKeyboardView(context : Context, attributeSet: AttributeSet) : FrameLayout(context,attributeSet) {
    private var listeners = mutableListOf<KeyboardListener>()
    private var charButtonsMap = mutableMapOf(
        R.id.button_0 to '0',
        R.id.button_1 to '1',
        R.id.button_2 to '2',
        R.id.button_3 to '3',
        R.id.button_4 to '4',
        R.id.button_5 to '5',
        R.id.button_6 to '6',
        R.id.button_7 to '7',
        R.id.button_8 to '8',
        R.id.button_9 to '9',
        R.id.button_a to 'A',
        R.id.button_b to 'B',
        R.id.button_c to 'C',
        R.id.button_d to 'D',
        R.id.button_e to 'E',
        R.id.button_f to 'F',
        R.id.button_space to ' ',
        R.id.button_x to 'x'
    )
    private var specialKeysMap = mutableMapOf(
        R.id.button_backspace to KeyEvent.KEYCODE_DEL
    )

    init {
        inflate(context,R.layout.hex_keyboard,this)
        setupKeyboard()
    }

    private fun setupKeyboard() {
        specialKeysMap.forEach {
            val button = findViewById<Button>(it.key)
            val keyCode = it.value

            button?.setOnClickListener { notifySpecialKeyDown(keyCode) }
        }

        charButtonsMap.forEach {
            val button = findViewById<Button>(it.key)
            button?.setOnClickListener { _ -> notifyCharTyped(it.value) }
        }
    }

    private fun notifyCharTyped(character: Char) {
        listeners.forEach { it.onCharTyped(character) }
    }

    private fun notifySpecialKeyDown(keyCode: Int) {
        val event = KeyEvent(KeyEvent.ACTION_DOWN,keyCode)
        listeners.forEach { it.onSpecialKeyDown(keyCode,event) }
    }

    fun addListener(vararg keyboardListener: KeyboardListener) {
        listeners.addAll(keyboardListener)
    }

    interface KeyboardListener {
        fun onSpecialKeyDown(keyCode : Int, keyEvent: KeyEvent)
        fun onCharTyped(character : Char)
    }

}