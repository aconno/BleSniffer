package com.aconno.hexinputlib

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintSet
import com.aconno.hexinputlib.ui.keyboard.HexKeyboardView

/**
 * Creates a layout that contains layout inflated from layout resource specified by [layoutResourceId]
 * parameter and an instance of [HexKeyboardView] that is attached to bottom of the created layout. Then,
 * it sets the created layout as receiver's content view.
 *
 * There is also optional parameter [wrapContentInScrollView] that specifies if the inflated layout
 * should be wrapped in a scroll view, so that it does not happen that part of the inflated layout is
 * covered by [HexKeyboardView] and therefore can not be accessed while the [HexKeyboardView] is displayed.
 * This parameter can be set to false if the layout that is to be inflated already contains a scroll view
 * (i.e. it itself handles scrolling it's content) or if the layout height is not big (so that the whole
 * layout content can be visible on the screen while the [HexKeyboardView] is displayed) or if it is
 * not important if the part of the inflated layout is inaccessible when the [HexKeyboardView] is
 * displayed.
 *
 * The purpose of this method is to be able to provide [HexKeyboardView] for [HexEditText][com.aconno.hexinputlib.ui.editor.HexEditText]
 * without adding it manually into activity content view. But since this method wraps the specified layout
 * into a new layout, this could lead do some performance issues or maybe even to some bugs, so use this
 * method with caution. This method is recommended to be used only for simple layouts, but if there is
 * some more complex layout, then the best option is to manually add a [HexKeyboardView] into the layout
 * resource.
 *
 * @receiver activity that needs a hex keyboard view added into it's content view
 * @param layoutResourceId  id of a layout resource to inflate layout from and set it into receiver's content view
 * @param wrapContentInScrollView true if the inflated layout (specified by [layoutResourceId] parameter) should
 *   be wrapped into a scroll view
 */
fun Activity.setContentViewWithHexKeyboardAutoAdded(layoutResourceId : Int, wrapContentInScrollView : Boolean = false) {
    val inflatedLayout = this.layoutInflater.inflate(layoutResourceId,null,false)

    val activityMainContent= if(wrapContentInScrollView) {
        inflatedLayout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        ScrollView(this).apply {
            addView(inflatedLayout)
        }
    } else {
        inflatedLayout
    }
    activityMainContent.apply {
        if(id==0) {
            id = View.generateViewId()
        }
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0)
    }

    val hexKeyboardView = HexKeyboardView(this).apply {
        id = View.generateViewId()
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        visibility = View.GONE
    }

    val contentView = androidx.constraintlayout.widget.ConstraintLayout(this).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        addView(activityMainContent)
        addView(hexKeyboardView)
    }

    ConstraintSet().apply {
        clone(contentView)
        connect(activityMainContent.id,ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP)
        connect(activityMainContent.id,ConstraintSet.BOTTOM,hexKeyboardView.id,ConstraintSet.TOP)
        connect(hexKeyboardView.id,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM)

        applyTo(contentView)
    }

    setContentView(contentView)
}

/**
 * Handles back button press in the following way: if a hex keyboard is displayed, it hides it, but
 * if it is not displayed, it finishes the activity that is the receiver of this extension function.
 *
 * @receiver an activity containing a hex keyboard in it's content view
 */
fun Activity.handleBackPressedWithHexKeyboardInContentView() {
    val hexKeyboardView = KeyboardManager.findHexKeyboardView(findViewById(android.R.id.content))
    if(hexKeyboardView.visibility == View.VISIBLE) {
        KeyboardManager.hideHexKeyboard(hexKeyboardView)
    } else {
        finish()
    }
}

internal fun Char.isHexChar() : Boolean {
    return HexUtils.HEX_CHARS.contains(this)
}