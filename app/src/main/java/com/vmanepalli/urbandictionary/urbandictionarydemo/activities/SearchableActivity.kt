package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

/**
 *
 * Initial load of this activity asks MeaningsViewModel for meanings of 'number'.
 * Whenever user searches for meanings for any word, it will ask MeaningsViewModel to find in local DB and also from Urban Dictionary via api call, in the order stated.
 *
 **/

import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmanepalli.urbandictionary.urbandictionarydemo.*
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModel
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModelFactory
import kotlinx.android.synthetic.main.activity_searchable.*
import kotlinx.android.synthetic.main.content_searchable.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchableActivity : AppCompatActivity() {

    //region Variable declarations
    private var progress: MenuItem? = null
    private var refreshItem: MenuItem? = null
    private var sortItem: MenuItem? = null
    private var searchActionView: SearchView? = null

    private val meaningViewModel: MeaningViewModel by lazy {
        ViewModelProvider(
            this,
            MeaningViewModelFactory(application)
        ).get(MeaningViewModel::class.java)
    }
    //endregion

    //region Activity override functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        setSupportActionBar(toolbar)
        recycler_view.configure()
        addObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_searchable, menu)
        progress = menu?.findItem(R.id.progress)
        refreshItem = menu?.findItem(R.id.refresh)
        sortItem = menu?.findItem(R.id.sort)

        val menuSearch = menu?.findItem(R.id.menu_search)
        menuSearch?.let { searchMenuItem ->
            (searchMenuItem.actionView as SearchView).apply {
                configure()
            }
        }?.let { searchView ->
            searchView.clearFocus()
            searchActionView = searchView
        }
        updateSortIcon(application.ascendingOrder)
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recycler_view.addDivider(application, newConfig)
        searchActionView?.clearFocus()
    }
    //endregion

    //region Menu Items helper methods
    // Only sorting either in asc or desc order of thumbs_up by maintaining a
    // flag for storing previous state.
    fun sortMeanings(item: MenuItem) {
        val order = meaningViewModel.sortMeanings()
        updateSortIcon(order)
    }

    // Refresh does API calls only to see if there any new entries available
    fun refresh(item: MenuItem) {
        showProgress()
        meaningViewModel.refresh { hideProgress() }
    }
    //endregion

    //region Private helper functions
    // Observers Meanings Live Data list
    // Notifies  when updates are available
    private fun addObserver() {
        meaningViewModel.getAllMeanings().observe(this, Observer {
            meaningViewModel.replaceAdapterData(it ?: listOf())
            meaningViewModel.notifyDataChange()
        })
    }

    // Update the sort icon to reflect the state of the sort used
    private fun updateSortIcon(inOrder: Boolean) {
        sortItem?.icon = if (inOrder) {
            resources.getDrawable(R.drawable.ic_asc_order, theme)
        } else {
            resources.getDrawable(R.drawable.ic_desc_order, theme)
        }
    }
    //endregion

    //region Synchronized Private Functions
    @Synchronized
    private fun showProgress() {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            progress.show()
            refreshItem.hide()
        }
    }

    @Synchronized
    private fun hideProgress() {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            progress.hide()
            refreshItem.show()
        }
    }
    //endregion

    //region Private Extensions
    private fun SearchView.configure() {
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
                clearFocus()
                this@SearchableActivity.showProgress()
                meaningViewModel.searchMeanings(query) {
                    this@SearchableActivity.hideProgress()
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return newText.isNotEmpty()
            }
        })
    }

    private fun RecyclerView.configure() {
        layoutManager = LinearLayoutManager(this@SearchableActivity)
        setHasFixedSize(true)
        addDivider(application, resources.configuration)
        adapter = meaningViewModel.meaningsAdapter
    }
    //endregion

    //region Companion
    companion object {
        const val SORT_ORDER = "AscendingOrder"
    }
    //endregion

}

