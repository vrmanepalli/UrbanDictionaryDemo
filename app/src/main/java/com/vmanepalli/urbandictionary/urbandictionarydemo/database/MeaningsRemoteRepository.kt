package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import com.vmanepalli.urbandictionary.urbandictionarydemo.api.DictionaryAPI
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class MeaningsRemoteRepository {

    private val service: DictionaryAPI =
        Retrofit.Builder().baseUrl(UD_API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(DictionaryAPI::class.java)

    fun getMeanings(term: String): Observable<List<Meaning>> {
        return service.getMeanings(term)
            .subscribeOn(Schedulers.io())
            .map { it.list }
            .toObservable()
    }

    companion object {
        val UD_API_ENDPOINT = "https://mashape-community-urban-dictionary.p.rapidapi.com/"
    }

}
