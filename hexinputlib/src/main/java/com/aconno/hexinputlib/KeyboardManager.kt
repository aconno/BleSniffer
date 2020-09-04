package com.aconno.hexinputlib

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.aconno.hexinputlib.ui.keyboard.BaseHexKeyboardView
import java.lang.IllegalStateException

object KeyboardManager {
    private const val DEFAULT_SHOW_KEYBOARD_DELAY_MILLIS = 500L
    private const val KEYBOARD_VIEW_MISSING_EXCEPTION_MESSAGE = "Unable to find hex keyboard view in activity content view hierarchy. To fix this issue, add HexKeyboardView into activity layout or replace Activity#setContentView() with Activity#setContentViewWithHexKeyboardAutoAdded() to make it added automatically."
    private const val KEYBOARD_REAPPEARANCE_TIME_THRESHOLD = 100

    private var keyboardHideEventTime : Long = 0
    private var showKeyboardDelayMillis : Long = DEFAULT_SHOW_KEYBOARD_DELAY_MILLIS

    fun findHexKeyboardView(viewHierarchyMember : View) : BaseHexKeyboardView {
        return findHexKeyboardInViewHierarchy(viewHierarchyMember.rootView) ?:
                    throw IllegalStateException(KEYBOARD_VIEW_MISSING_EXCEPTION_MESSAGE)
    }

    private fun findHexKeyboardInViewHierarchy(root : View) : BaseHexKeyboardView? {
        if(root is BaseHexKeyboardView) {
            return root
        } else if(root is ViewGroup) {
            for(i in 0 until root.childCount) {
                val hexKeyboard = findHexKeyboardInViewHierarchy(root.getChildAt(i))
                if(hexKeyboard != null) {
                    return hexKeyboard
                }
            }
        }
        return null
    }

    fun showHexKeyboard(hexKeyboardView: BaseHexKeyboardView) {
        hideSystemKeyboard(hexKeyboardView)

        if(hexKeyboardView.visibility != View.VISIBLE) {
            if(System.currentTimeMillis() - keyboardHideEventTime > KEYBOARD_REAPPEARANCE_TIME_THRESHOLD) {
                Handler().postDelayed({
                    hexKeyboardView.visibility = View.VISIBLE
                }, showKeyboardDelayMillis)
            } else {
                hexKeyboardView.visibility = View.VISIBLE
            }

        }
    }

    fun showHexKeyboard(viewRequestingKeyboard : View) {
        showHexKeyboard(findHexKeyboardView(viewRequestingKeyboard))
    }

    fun hideHexKeyboard(hexKeyboardView: BaseHexKeyboardView) {
        hexKeyboardView.visibility = View.GONE
        keyboardHideEventTime = System.currentTimeMillis()
    }

    fun hideHexKeyboard(viewToHideFrom: View) {
        hideHexKeyboard(findHexKeyboardView(viewToHideFrom))
    }

    fun hideSystemKeyboard(viewToHideFrom: View) {
        val inputMethodManager = viewToHideFrom.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(viewToHideFrom.windowToken,0)
    }

    fun setHexKeyboardShowDelay(delayMillis : Long) {
        showKeyboardDelayMillis = delayMillis
    }

}