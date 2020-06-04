package com.vmanepalli.urbandictionary.urbandictionarydemo.activities

/**
 *
 * Initial load of this activity asks MeaningsViewModel for meanings of 'number'.
 * Whenever user searches for meanings for any word, it will ask MeaningsViewModel to find in local DB and also from Urban Dictionary via api call, in the order stated.
 *
 **/

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vmanepalli.urbandictionary.urbandictionarydemo.*
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModel
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModelFactory
import com.vmanepalli.urbandictionary.urbandictionarydemo.views.DictionarySearchView
import kotlinx.android.synthetic.main.activity_searchable.*
import kotlinx.android.synthetic.main.content_searchable.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface SearchListener {
    fun submitQuery(query: String)
}

class SearchableActivity : AppCompatActivity(), SearchListener {

    //region Variable declarations
    private lateinit var progress: MenuItem
    private lateinit var refreshItem: MenuItem
    private lateinit var sortItem: MenuItem
    private lateinit var searchActionView: DictionarySearchView

    private val meaningViewModel: MeaningViewModel by lazy {
        ViewModelProvider(
            this,
            MeaningViewModelFactory(application)
        ).get(MeaningViewModel::class.java)
    }
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)
        setSupportActionBar(toolbar)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recycler_view.addDivider(application, newConfig)
    }
    //endregion

    //region Options Menu functions

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_searchable, menu)

        menu?.let { thisMenu ->
            progress = thisMenu.findItem(R.id.progress)
            refreshItem = thisMenu.findItem(R.id.refresh)

            thisMenu.findItem(R.id.sort)?.let { sortMenuItem ->
                sortItem = sortMenuItem
                setSortIcon(application.ascendingOrder)
            }

            thisMenu.findItem(R.id.menu_search)?.let { searchMenuItem ->
                (searchMenuItem.actionView as DictionarySearchView).apply {
                    setComponentName(componentName)
                    setSearchListener(this@SearchableActivity)
                }
            }?.let { searchView ->
                searchActionView = searchView
                searchActionView.clearFocus()
            }
        }
        observeModel()
        recycler_view.configure()
        return true
    }

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

    //region ViewModel functions
    // Observers Meanings Live Data list
    // Notifies  when updates are available
    private fun observeModel() {
        meaningViewModel.getAllMeanings().observe(this, Observer {
            it?.let {
                hideProgress()
            }
        })
    }

    // Only sorting either in asc or desc order of thumbs_up by maintaining a
    // flag for storing previous state.
    fun flipSort(i: MenuItem) {
        val order = meaningViewModel.flipSort()
        setSortIcon(order)
    }

    // Update the sort icon to reflect the state of the sort used
    private fun setSortIcon(inOrder: Boolean) {
        sortItem.icon = if (inOrder) {
            resources.getDrawable(R.drawable.ic_asc_order, theme)
        } else {
            resources.getDrawable(R.drawable.ic_desc_order, theme)
        }
    }

    // Refresh does API calls only to see if there any new entries available
    fun refresh(i: MenuItem) {
        showProgress()
        meaningViewModel.refresh()
    }

    @Synchronized
    override fun submitQuery(query: String) {
        showProgress()
        meaningViewModel.searchMeanings(query)
    }
    //endregion

    //region Private Extensions

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

