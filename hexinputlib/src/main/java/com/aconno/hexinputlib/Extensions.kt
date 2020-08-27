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
    activityMainContent.id = View.generateViewId()
    activityMainContent.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0)

    val hexKeyboardView = HexKeyboardView(this)
    hexKeyboardView.id = View.generateViewId()
    hexKeyboardView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

    val contentView = androidx.constraintlayout.widget.ConstraintLayout(this)
    contentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    contentView.addView(activityMainContent)
    contentView.addView(hexKeyboardView)

    val constraintSet = ConstraintSet()
    constraintSet.clone(contentView)
    constraintSet.connect(activityMainContent.id,ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP)
    constraintSet.connect(activityMainContent.id,ConstraintSet.BOTTOM,hexKeyboardView.id,ConstraintSet.TOP)
    constraintSet.connect(hexKeyboardView.id,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM)

    constraintSet.applyTo(contentView)

    setContentView(contentView)
}

internal fun Char.isHexChar() : Boolean {
    return HexUtils.HEX_CHARS.contains(this)
}