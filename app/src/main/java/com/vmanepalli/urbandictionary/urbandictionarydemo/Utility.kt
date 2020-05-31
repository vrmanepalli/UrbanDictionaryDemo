package com.vmanepalli.urbandictionary.urbandictionarydemo

import android.app.Application
import android.content.res.Configuration
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun Application.toast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun Configuration.orientationForRecyclerView(): Int {
    // The integer values of recyclerview orientation are offset by 1 than
    // screen configuration orientation. So performing this check.
    return if (orientation == 1) {
        DividerItemDecoration.VERTICAL
    } else {
        DividerItemDecoration.HORIZONTAL
    }
}

fun RecyclerView.addDivider(applicationContext: Application, newConfig: Configuration) {
    val dividerItemDecoration =
        DividerItemDecoration(applicationContext, newConfig.orientationForRecyclerView())
    this.addItemDecoration(dividerItemDecoration)
}

