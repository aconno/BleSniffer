package com.aconno.hexinputlib

import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.aconno.hexinputlib.ui.keyboard.BaseHexKeyboardView
import java.lang.IllegalStateException

/**
 * An object providing methods related to handling a hex keyboard and the system keyboard. To be more
 * specific, it contains methods that handle showing and hiding a hex keyboard and the system keyboard,
 * with some other helpful methods as well. It is highly recommended to use methods of this manager to
 * handle displaying hex keyboard instead of doing this manually (for example, by directly changing the
 * visibility of a [BaseHexKeyboardView]) because doing this manually could lead to some issues resulting
 * in a poor UX.
 */
object KeyboardManager {
    private var keyboardHideEventTime : Long = 0

    /**
     * Finds a [BaseHexKeyboardView] in the view hierarchy of [viewHierarchyMember]. It goes through
     * the whole hierarchy to find any [BaseHexKeyboardView], not only through the descendants of the
     * [viewHierarchyMember]. If there is no any [BaseHexKeyboardView] in the view hierarchy, an
     * [IllegalStateException] is thrown.
     *
     * @param viewHierarchyMember member of a view hierarchy that is expected to contain a [BaseHexKeyboardView]
     *
     * @return an instance of [BaseHexKeyboardView] found in the view hierarchy of [viewHierarchyMember]
     * @throws IllegalStateException if there is no any [BaseHexKeyboardView] in the view hierarchy
     */
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

    /**
     * Displays the specified [hexKeyboardView]. If the system keyboard is open when this method
     * gets called, the method will automatically hide it before displaying the [hexKeyboardView].
     *
     * @param hexKeyboardView a hex keyboard that should be displayed
     *
     */
    fun showHexKeyboard(hexKeyboardView: BaseHexKeyboardView) {
        hideSystemKeyboard(hexKeyboardView)

        if(hexKeyboardView.visibility != View.VISIBLE) {
            if(System.currentTimeMillis() - keyboardHideEventTime > KEYBOARD_REAPPEARANCE_TIME_THRESHOLD) {
                Handler().postDelayed({
                    hexKeyboardView.visibility = View.VISIBLE
                }, SHOW_KEYBOARD_DELAY_MILLIS)
            } else {
                hexKeyboardView.visibility = View.VISIBLE
            }

        }
    }

    /**
     * Displays a [BaseHexKeyboardView] contained in the view hierarchy of [viewRequestingKeyboard].
     * If the system keyboard is open when this method gets called, the method will automatically
     * hide it before displaying the [BaseHexKeyboardView].
     *
     * @param viewRequestingKeyboard view that needs to use a hex keyboard
     */
    fun showHexKeyboard(viewRequestingKeyboard : View) {
        showHexKeyboard(findHexKeyboardView(viewRequestingKeyboard))
    }

    /**
     * Hides the specified [hexKeyboardView].
     *
     * @param hexKeyboardView a hex keyboard view that is to be hidden
     */
    fun hideHexKeyboard(hexKeyboardView: BaseHexKeyboardView) {
        hexKeyboardView.visibility = View.GONE
        keyboardHideEventTime = System.currentTimeMillis()
    }

    /**
     * Hides a [BaseHexKeyboardView] contained in the view hierarchy of [viewToHideFrom]. If there
     * is no any [BaseHexKeyboardView] in the view hierarchy, an [IllegalStateException] is thrown.
     *
     * @param viewToHideFrom member of view hierarchy containing a [BaseHexKeyboardView] that is to be hidden
     *
     * @throws IllegalStateException if there is no any [BaseHexKeyboardView] in the view hierarchy of [viewToHideFrom]
     */
    fun hideHexKeyboard(viewToHideFrom: View) {
        hideHexKeyboard(findHexKeyboardView(viewToHideFrom))
    }

    /**
     * Hides the system keyboard from [viewToHideFrom].
     *
     * @param viewToHideFrom view to hide the system keyboard from
     */
    fun hideSystemKeyboard(viewToHideFrom: View) {
        val inputMethodManager = viewToHideFrom.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(viewToHideFrom.windowToken,0)
    }

    private const val KEYBOARD_VIEW_MISSING_EXCEPTION_MESSAGE = "Unable to find hex keyboard view in activity content view hierarchy. To fix this issue, add HexKeyboardView into activity layout or replace Activity#setContentView() with Activity#setContentViewWithHexKeyboardAutoAdded() to make it added automatically."
    private const val SHOW_KEYBOARD_DELAY_MILLIS = 500L
    private const val KEYBOARD_REAPPEARANCE_TIME_THRESHOLD = 100
}