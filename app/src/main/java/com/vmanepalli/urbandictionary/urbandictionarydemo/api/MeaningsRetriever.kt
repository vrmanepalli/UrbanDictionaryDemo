package com.vmanepalli.urbandictionary.urbandictionarydemo.api

import com.vmanepalli.urbandictionary.urbandictionarydemo.models.MeaningList
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MeaningsRetriever {
    private val service: DictionaryAPI

    init {
        val retrofit =
            Retrofit.Builder().baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(DictionaryAPI::class.java)
    }

    fun getMeanings(callback: Callback<MeaningList>, term: String) {
        val call = service.getMeanings(term)
        call.enqueue(callback)
    }

    companion object {
        const val baseURL = "https://mashape-community-urban-dictionary.p.rapidapi.com/"
    }

}
