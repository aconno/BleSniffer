package com.aconno.blesniffer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.Button
import android.widget.FrameLayout
import com.aconno.blesniffer.R

class HexKeyboardView(context : Context, attributeSet: AttributeSet) : FrameLayout(context,attributeSet) {
    private var listeners = mutableListOf<KeyboardListener>()
    private var buttonKeyCodes = mutableMapOf<Int,Int>(
        R.id.button_0 to KeyEvent.KEYCODE_0,
        R.id.button_1 to KeyEvent.KEYCODE_1,
        R.id.button_2 to KeyEvent.KEYCODE_2,
        R.id.button_3 to KeyEvent.KEYCODE_3,
        R.id.button_4 to KeyEvent.KEYCODE_4,
        R.id.button_5 to KeyEvent.KEYCODE_5,
        R.id.button_6 to KeyEvent.KEYCODE_6,
        R.id.button_7 to KeyEvent.KEYCODE_7,
        R.id.button_8 to KeyEvent.KEYCODE_8,
        R.id.button_9 to KeyEvent.KEYCODE_9,
        R.id.button_a to KeyEvent.KEYCODE_A,
        R.id.button_b to KeyEvent.KEYCODE_B,
        R.id.button_c to KeyEvent.KEYCODE_C,
        R.id.button_d to KeyEvent.KEYCODE_D,
        R.id.button_e to KeyEvent.KEYCODE_E,
        R.id.button_f to KeyEvent.KEYCODE_F,
        R.id.button_space to KeyEvent.KEYCODE_SPACE,
        R.id.button_backspace to KeyEvent.KEYCODE_DEL,
        R.id.button_x to KeyEvent.KEYCODE_X
    )

    init {
        inflate(context,R.layout.hex_keyboard,this)
        setupKeyboard()
    }


    private fun setupKeyboard() {
        buttonKeyCodes.forEach {
            val button = findViewById<Button>(it.key)
            val keyCode = it.value

            button?.setOnClickListener { notifyKeyDown(keyCode) }
        }
    }

    private fun notifyKeyDown(keyCode: Int) {
        val event = KeyEvent(KeyEvent.ACTION_DOWN,keyCode)
        listeners.forEach { it.onKeyDown(keyCode,event) }
    }

    fun addListener(vararg keyboardListener: KeyboardListener) {
        listeners.addAll(keyboardListener)
    }

    interface KeyboardListener {
        fun onKeyDown(keyCode : Int, keyEvent: KeyEvent)
    }

}