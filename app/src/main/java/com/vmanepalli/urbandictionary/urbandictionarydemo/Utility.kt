package com.vmanepalli.urbandictionary.urbandictionarydemo

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

inline fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}