package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmanepalli.urbandictionary.urbandictionarydemo.MeaningsAdapter
import com.vmanepalli.urbandictionary.urbandictionarydemo.R
import com.vmanepalli.urbandictionary.urbandictionarydemo.hideKeyboard
import com.vmanepalli.urbandictionary.urbandictionarydemo.isConnected
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModel
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var meaningViewModel: MeaningViewModel? = null
    private var meaningsAdapter: MeaningsAdapter? = null
    private var searchTerm = "number"
    private var ascendingOrder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setSearchListener()
        configureRecyclerView()
        addObserver()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        addDivider(newConfig.orientation)
    }

    private fun configureRecyclerView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        addDivider(resources.configuration.orientation)
    }

    private fun addDivider(orientationNow: Int) {
        val decorationO = if (orientationNow == 1) {
            DividerItemDecoration.VERTICAL
        } else {
            DividerItemDecoration.HORIZONTAL
        }
        val dividerItemDecoration =
            DividerItemDecoration(applicationContext, decorationO)
        recycler_view.addItemDecoration(dividerItemDecoration)
    }

    private fun addObserver() {
        meaningViewModel = ViewModelProvider(
            this,
            MeaningViewModelFactory(application, searchTerm)
        ).get(MeaningViewModel::class.java)
        meaningViewModel?.getAllMeanings()?.observe(this, Observer {
            progress.visibility = View.INVISIBLE
            notifyAdapter(it)
        })
    }

    private fun notifyAdapter(it: List<Meaning>?) {
        val results = it ?: listOf()
        if (meaningsAdapter == null) {
            meaningsAdapter = MeaningsAdapter(results)
            recycler_view.adapter = meaningsAdapter
        } else {
            meaningsAdapter?.meanings = results
            meaningsAdapter?.notifyDataSetChanged()
        }
    }

    private fun setSearchListener() {
        search.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search.hideKeyboard()
                progress.visibility = View.INVISIBLE
                searchTerm = search.text.toString()
                meaningViewModel?.searchMeaning(searchTerm, isConnected)
                return@OnKeyListener true
            }
            false
        })
        search.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
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
