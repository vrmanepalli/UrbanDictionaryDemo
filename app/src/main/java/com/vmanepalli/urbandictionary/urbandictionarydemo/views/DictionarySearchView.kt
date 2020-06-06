package com.vmanepalli.urbandictionary.urbandictionarydemo.views

import android.app.SearchManager
import android.app.SearchManager.SUGGEST_COLUMN_TEXT_1
import android.content.ComponentName
import android.content.Context
import android.content.res.Configuration
import android.database.Cursor
import android.provider.SearchRecentSuggestions
import android.widget.SearchView
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchListener
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.MeaningSuggestionProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Suggestions

/**
 * @author vmanepalli
 * Date: 2020-06-02
 * Time: 16:46
 */
class DictionarySearchView(context: Context) : SearchView(context) {

    private val appContext: Context = context
    private lateinit var searchListener: SearchListener

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
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchListener.findSuggestions("$it%")
                        return true
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchListener.submitQuery(it)
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
                val cursor = suggestionsAdapter.getItem(p0) as Cursor
                val query = cursor.getString(cursor.getColumnIndex(SUGGEST_COLUMN_TEXT_1))
                setQuery(query, true)
                return true
            }
        })

        setOnCloseListener {
            clearFocus()
            setQuery("", false)
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        clearFocus()
    }

    fun setComponentName(name: ComponentName) {
        val searchManager = appContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(name))
    }

    fun updateSuggestions(suggestions: List<Suggestions>) {
        suggestions.stream().forEach {
            recentSuggestions.saveRecentQuery(it.word, it.definition)
        }
    }

    fun setSearchListener(searchableListener: SearchListener) {
        this.searchListener = searchableListener
    }
}