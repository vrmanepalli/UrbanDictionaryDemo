package com.vmanepalli.urbandictionary.urbandictionarydemo.datasource

import android.content.SearchRecentSuggestionsProvider

/**
 * @author vmanepalli
 * Date: 2020-06-04
 * Time: 21:52
 */
class MeaningSuggestionProvider : SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY =
            "com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.MeaningSuggestionProvider"
        const val MODE =
            DATABASE_MODE_2LINES or DATABASE_MODE_QUERIES
    }

}