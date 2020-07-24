package com.aconno.blesniffer.ui

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class KeyboardManager {
    private var keyboardHideEventTime : Long = 0L

    fun manageKeyboardForActivity(activity : Activity, activityContentView : View, hexKeyboardView: HexKeyboardView) {
        getAllEditTextViews(activityContentView).filter { it.tag == HEX_INPUT_TAG }.forEach {
            setupHexInputEditText(it,activity,hexKeyboardView)
        }
    }

    fun onBackPressed(hexKeyboardView: HexKeyboardView) : Boolean {
        if(hexKeyboardView.visibility == View.VISIBLE) {
            hideKeyboard(hexKeyboardView)
            return true
        }
        return false
    }

    private fun setupHexInputEditText(input: EditText, activity: Activity, hexKeyboardView: HexKeyboardView) {
        input.showSoftInputOnFocus = false
        input.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) {
                (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken,0)

                showKeyboard(hexKeyboardView,activity)
            } else {
                hideKeyboard(hexKeyboardView)
            }
        }
        input.setOnClickListener {
            showKeyboard(hexKeyboardView,activity)
        }

        hexKeyboardView.addListener(object : HexKeyboardView.KeyboardListener {
            private val inputConnection = input.onCreateInputConnection(EditorInfo())

            override fun onSpecialKeyDown(keyCode: Int,keyEvent: KeyEvent) {
                if(input.hasFocus()) {
                    input.onKeyDown(keyCode,keyEvent)
                }
            }

            override fun onCharTyped(character: Char) {
                if(input.hasFocus()) {
                    inputConnection.commitText(character.toString(),1)
                }
            }
        })

    }

    private fun getAllEditTextViews(root : View) : List<EditText> {
        if(root is EditText) {
            return listOf(root)
        } else if(root is ViewGroup) {
            val editTextViews = mutableListOf<EditText>()
            for(i in 0 until root.childCount) {
                editTextViews.addAll(getAllEditTextViews(root.getChildAt(i)))
            }
            return editTextViews
        }
        return listOf()
    }

    private fun hideKeyboard(hexKeyboardView: HexKeyboardView) {
        hexKeyboardView.visibility = View.GONE
        keyboardHideEventTime = System.currentTimeMillis()
    }

    private fun showKeyboard(hexKeyboardView: HexKeyboardView, activity: Activity) {
        if(hexKeyboardView.visibility != View.VISIBLE) {
            hexKeyboardView.visibility = View.VISIBLE

            if(System.currentTimeMillis() - keyboardHideEventTime > ANIMATED_KEYBOARD_SHOW_UP_TIME_THRESHOLD) {
                hexKeyboardView.y = activity.resources.displayMetrics.heightPixels.toFloat()
                hexKeyboardView.animate().setStartDelay(SHOW_KEYBOARD_DELAY_MILLIS).translationY(0f)
            }
        }
    }

    companion object {
        const val HEX_INPUT_TAG = "hexInput"
        const val SHOW_KEYBOARD_DELAY_MILLIS = 300L
        const val ANIMATED_KEYBOARD_SHOW_UP_TIME_THRESHOLD = 100
    }
}