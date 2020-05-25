package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vmanepalli.urbandictionary.urbandictionarydemo.R
import com.vmanepalli.urbandictionary.urbandictionarydemo.hideKeyboard
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setSearchListener()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    private fun setSearchListener() {
        search.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search.hideKeyboard()
                showUnderDevToast()
                return@OnKeyListener true
            }
            false
        })
    }

    fun sort(view: View?) {
        showUnderDevToast()
    }

    fun refresh(view: View?) {
        showUnderDevToast()
    }

    private fun showUnderDevToast() {
        Toast.makeText(
            applicationContext,
            "Not available! Development in progress..!",
            Toast.LENGTH_SHORT
        ).show()
    }

}
