package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

/**
 *
 * Initial load of this activity asks MeaningsViewModel for meanings of 'number'.
 * Whenever user searches for meanings for any word, it will ask MeaningsViewModel to find in local DB and also from Urban Dictionary via api call, in the order stated.
 *
 **/

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vmanepalli.urbandictionary.urbandictionarydemo.MeaningsAdapter
import com.vmanepalli.urbandictionary.urbandictionarydemo.R
import com.vmanepalli.urbandictionary.urbandictionarydemo.addDivider
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModel
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModelFactory
import kotlinx.android.synthetic.main.activity_searchable.*
import kotlinx.android.synthetic.main.content_searchable.*


class SearchableActivity : AppCompatActivity() {

    private var ascendingOrder = true
    private var progress: MenuItem? = null
    private var refreshItem: MenuItem? = null
    private var sortItem: MenuItem? = null
    private var searchActionView: SearchView? = null
    private var isConnected = true

    private val meaningsAdapter: MeaningsAdapter by lazy { MeaningsAdapter(listOf()) }

    private val meaningViewModel: MeaningViewModel by lazy {
        ViewModelProvider(
            this,
            MeaningViewModelFactory(application)
        ).get(MeaningViewModel::class.java)
    }

    private val broadcastReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                isConnected = intent.getBooleanExtra(
                    ConnectivityManager
                        .EXTRA_NO_CONNECTIVITY, false
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        setSupportActionBar(toolbar)
        configureRecyclerView()
        addObserver()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.EXTRA_NO_CONNECTIVITY))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recycler_view.addDivider(application, newConfig)
        searchActionView?.clearFocus()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_searchable, menu)
        progress = menu?.findItem(R.id.progress)
        refreshItem = menu?.findItem(R.id.refresh)
        sortItem = menu?.findItem(R.id.sort)

        val menuSearch = menu?.findItem(R.id.menu_search)
        menuSearch?.let { searchMenuItem ->
            (searchMenuItem.actionView as SearchView).apply {
                configureSearchView()
            }
        }?.let { searchView ->
            searchView.clearFocus()
            searchActionView = searchView
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    // Only sorting either in asc or desc order of thumbs_up by maintaining a
    // flag for storing previous state.
    fun sort(item: MenuItem) {
        with(meaningsAdapter) {
            if (meanings.isEmpty()) {
                return
            }
            sort(!ascendingOrder)
            notifyDataSetChanged()
        }
        ascendingOrder = !ascendingOrder
        sortItem?.icon = if (ascendingOrder)  {
            resources.getDrawable(R.drawable.ic_asc_order, theme)
        } else {
            resources.getDrawable(R.drawable.ic_desc_order, theme)
        }
    }

    // Refresh does API calls only to see if there any new entries available
    fun refresh(item: MenuItem) {
        showProgress()
        meaningViewModel.refresh(isConnected) { hideProgress() }
    }

    // MARK - Private helper functions

    private fun configureRecyclerView() {
        with(recycler_view) {
            layoutManager = LinearLayoutManager(this@SearchableActivity)
            setHasFixedSize(true)
            addDivider(application, resources.configuration)
            adapter = meaningsAdapter
        }
    }

    // Observers Meanings Live Data list
    // Notifies  when updates are available
    private fun addObserver() {
        meaningViewModel.getAllMeanings().observe(this, Observer {
            val results = it ?: listOf()
            meaningsAdapter.meanings = results.sortedBy { it.thumbs_up }
            meaningsAdapter.notifyDataSetChanged()
        })
    }

    private fun showProgress() {
        progress.show()
        refreshItem.hide()
    }

    private fun hideProgress() {
        progress.hide()
        refreshItem.show()
    }

    // MARK - Private Extensions

    private fun SearchView.configureSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(componentName))

        isIconified = false
        isQueryRefinementEnabled = true

        setOnCloseListener {
            clearFocus()
            setQuery("", false)
            true
        }

        setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                this@SearchableActivity.showProgress()
                meaningViewModel.searchMeanings(isConnected, query) {
                    this@SearchableActivity.hideProgress()
                    clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return newText.isNotEmpty()
            }
        })
    }

    private fun MenuItem?.show() {
        this?.let { isVisible = true }
    }

    private fun MenuItem?.hide() {
        this?.let { isVisible = false }
    }

}
