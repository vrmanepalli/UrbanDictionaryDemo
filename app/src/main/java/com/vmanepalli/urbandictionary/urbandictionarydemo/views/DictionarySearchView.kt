package com.vmanepalli.urbandictionary.urbandictionarydemo.views

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.widget.SearchView
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchListener

/**
 * @author vmanepalli
 * Date: 2020-06-02
 * Time: 16:46
 */
class DictionarySearchView(context: Context?) : SearchView(context) {

    private lateinit var appContext: Context
    private lateinit var searchListener: SearchListener

    init {

        context?.let { appContext = it }
        isIconified = false
        isQueryRefinementEnabled = true

        setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return newText?.isNotEmpty() ?: false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                clearFocus()
                query?.let { searchListener.submitQuery(it) }
                return true
            }
        })

        setOnCloseListener {
            clearFocus()
            setQuery("", false)
            true
        }
    }

    fun setComponentName(name: ComponentName) {
        val searchManager = appContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(name))
    }

    fun setSearchListener(listener: SearchListener) {
        searchListener = listener
    }
}