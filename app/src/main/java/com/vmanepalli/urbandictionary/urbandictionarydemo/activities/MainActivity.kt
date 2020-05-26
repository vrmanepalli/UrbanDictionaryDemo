package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

/**
 *
 * Initial load of this activity asks MeaningsViewModel for meanings of 'number'.
 * Whenever user searches for meanings for any word, it will ask MeaningsViewModel to find in local DB and also from Urban Dictionary via api call, in the order stated.
 *
 **/

import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
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
        // The integer values of recyclerview orientation are offset by 1 than
        // screen configuration orientation. So performing this check.
        val decorationO = if (orientationNow == 1) {
            DividerItemDecoration.VERTICAL
        } else {
            DividerItemDecoration.HORIZONTAL
        }
        val dividerItemDecoration =
            DividerItemDecoration(applicationContext, decorationO)
        recycler_view.addItemDecoration(dividerItemDecoration)
    }

    // This observer for Meanings Live Data list, gets notified for updating UI,
    // whenever changes in DB; to this List takes place.
    // The changes could happen when MeaningViewModel queries DB or makes an API
    // call.
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

    // Initial order of meanings is unchanged and loaded as it is in API response
    // or DB query.
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

    // Search listener asks MeaningsViewModel to make DB and DB calls for user
    // entry, only after enter enter/go is clicked from keyboard.
    private fun setSearchListener() {
        search.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                search.hideKeyboard()
                progress.visibility = View.VISIBLE
                searchTerm = search.text.toString()
                meaningViewModel?.searchMeaning(isConnected, searchTerm)
                return@OnKeyListener true
            }
            false
        })
        search.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    // Only sorting either in asc or desc order of thumbs_up by maintaining a
    // flag for storing previous state.
    fun sort(view: View?) {
        meaningsAdapter?.sort(ascendingOrder)
        meaningsAdapter?.notifyDataSetChanged()
        ascendingOrder = !ascendingOrder
    }

    // Refresh does API calls only to see if there any new entries available
    fun refresh(view: View?) {
        if (searchTerm == null) {
            return
        }
        progress.visibility = View.VISIBLE
        meaningViewModel?.searchMeaningsOnline(isConnected, searchTerm)
    }

}
