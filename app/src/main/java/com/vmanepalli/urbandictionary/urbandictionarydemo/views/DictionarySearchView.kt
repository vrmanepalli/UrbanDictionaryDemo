package com.vmanepalli.urbandictionary.urbandictionarydemo.views

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.provider.SearchRecentSuggestions
import android.widget.SearchView
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchListener
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.MeaningSuggestionProvider

/**
 * @author vmanepalli
 * Date: 2020-06-02
 * Time: 16:46
 */
class DictionarySearchView(context: Context?) : SearchView(context) {

    private lateinit var appContext: Context
    private lateinit var searchListener: SearchListener
    private lateinit var suggestions: List<String>

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
                query?.let {
                    searchListener.submitQuery(it)
                    SearchRecentSuggestions(appContext, MeaningSuggestionProvider.AUTHORITY, MeaningSuggestionProvider.MODE).saveRecentQuery(query, null)
                }
                return true
            }
        })

        setOnCloseListener {
            clearFocus()
            setQuery("", false)
            true
        }

        setOnSuggestionListener(object : OnSuggestionListener {
            override fun onSuggestionClick(p0: Int): Boolean {
                print("Clicked on suggestion $p0")
                return true
            }

            override fun onSuggestionSelect(p0: Int): Boolean {
                clearFocus()
                val cursor = suggestionsAdapter.getItem(p0) as Cursor
                val query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                setQuery(query, true)
                return true
            }
        })
    }

    fun setComponentName(name: ComponentName) {
        val searchManager = appContext.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(name))
    }

    fun setSearchListener(listener: SearchListener) {
        searchListener = listener
    }
}