package com.aconno.hexinputlib

import android.app.Activity

fun Activity.setContentViewWithHexKeyboardAutoAdded(layoutResourceId : Int) {
    TODO()
}

internal fun Char.isHexChar() : Boolean {
    return HexUtils.HEX_CHARS.contains(this)
}