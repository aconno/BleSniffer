package com.aconno.hexinputlib

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintSet
import com.aconno.hexinputlib.ui.keyboard.HexKeyboardView

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
        id = View.generateViewId()
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