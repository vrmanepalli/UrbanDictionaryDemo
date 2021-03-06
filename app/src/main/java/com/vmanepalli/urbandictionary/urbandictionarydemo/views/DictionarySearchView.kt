package com.vmanepalli.urbandictionary.urbandictionarydemo.views

import android.app.SearchManager
import android.app.SearchManager.SUGGEST_COLUMN_TEXT_1
import android.app.SearchManager.SUGGEST_COLUMN_TEXT_2
import android.content.ComponentName
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.provider.SearchRecentSuggestions
import android.widget.SearchView
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchListener
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.MeaningSuggestionProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning

/**
 * @author vmanepalli
 * Date: 2020-06-02
 * Time: 16:46
 */
class DictionarySearchView(context: Context) : SearchView(context) {

    private lateinit var searchListener: SearchListener
    private val appContext: Context = context

    private val recentSuggestions: SearchRecentSuggestions = SearchRecentSuggestions(
        appContext,
        MeaningSuggestionProvider.AUTHORITY,
        MeaningSuggestionProvider.MODE
    )

    init {
        isIconified = false
        isQueryRefinementEnabled = true

        setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {term ->
                    searchListener.submitQuery(term)
                }
                clearFocus()
                return true
            }
        })

        setOnSuggestionListener(object : OnSuggestionListener {
            override fun onSuggestionSelect(p0: Int): Boolean {
                return true
            }

            override fun onSuggestionClick(p0: Int): Boolean {
                clearFocus()
                val cursor = suggestionsAdapter.getItem(p0) as Cursor
                val query = cursor.getString(cursor.getColumnIndex(SUGGEST_COLUMN_TEXT_1))
                val definition = cursor.getString(cursor.getColumnIndex(SUGGEST_COLUMN_TEXT_2))
                setQuery(query, false)
                searchListener.submitSuggestionQuery(query, definition)
                return true
            }
        })

        setOnCloseListener {
            clearFocus()
            setQuery("", false)
            true
        }

        clearFocus()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        clearFocus()
    }

    fun setComponentName(name: ComponentName) {
        val searchManager = appContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(name))
    }

    fun updateSuggestions(meanings: List<Meaning>) {
        meanings.stream().forEach {
            recentSuggestions.saveRecentQuery(it.word, it.definition)
        }
    }

    fun setSearchListener(searchableListener: SearchListener) {
        this.searchListener = searchableListener
    }
}