package com.vmanepalli.urbandictionary.urbandictionarydemo.views

import android.app.SearchManager
import android.app.SearchManager.*
import android.content.ComponentName
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.provider.SearchRecentSuggestions
import android.widget.AutoCompleteTextView
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import com.vmanepalli.urbandictionary.urbandictionarydemo.R
import com.vmanepalli.urbandictionary.urbandictionarydemo.activities.SearchListener
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.MeaningSuggestionProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Suggestions

/**
 * @author vmanepalli
 * Date: 2020-06-02
 * Time: 16:46
 */
class DictionarySearchView(context: Context?) : SearchView(context) {

    private lateinit var appContext: Context
    private lateinit var searchListener: SearchListener
    private lateinit var suggestions: List<Suggestions>

    init {

        context?.let { appContext = it }
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
                clearFocus()
                query?.let {
                    searchListener.submitQuery(it)
                    SearchRecentSuggestions(
                        appContext,
                        MeaningSuggestionProvider.AUTHORITY,
                        MeaningSuggestionProvider.MODE
                    ).saveRecentQuery(query, null)
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
                val cursor = suggestionsAdapter.getItem(p0) as Cursor
                val query =
                    cursor.getString(cursor.getColumnIndex(SUGGEST_COLUMN_TEXT_1))
                setQuery(query, true)
                return true
            }

            override fun onSuggestionSelect(p0: Int): Boolean {
                clearFocus()
                val cursor = suggestionsAdapter.getItem(p0) as Cursor
                val query =
                    cursor.getString(cursor.getColumnIndex(SUGGEST_COLUMN_TEXT_1))
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

    fun updateSuggestions(it: List<Suggestions>) {
        suggestions = it
        val from = arrayOf(SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_TEXT_2)
        val to = intArrayOf(R.id.word, R.id.definition)
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SUGGEST_COLUMN_TEXT_1, SUGGEST_COLUMN_TEXT_2))
        suggestions.forEachIndexed{ index, suggestion ->
            cursor.addRow(arrayOf(index, suggestion.word, suggestion.definition))
        }
        val cursorAdapter = SimpleCursorAdapter(appContext, R.layout.suggestions, cursor, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        suggestionsAdapter = cursorAdapter
        suggestionsAdapter.notifyDataSetChanged()
    }
}