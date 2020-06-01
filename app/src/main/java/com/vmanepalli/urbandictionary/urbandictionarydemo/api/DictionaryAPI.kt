package com.vmanepalli.urbandictionary.urbandictionarydemo.api

import com.vmanepalli.urbandictionary.urbandictionarydemo.models.MeaningList
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DictionaryAPI {

    @Headers(
        value = ["x-rapidapi-host: mashape-community-urban-dictionary.p.rapidapi.com",
            "x-rapidapi-key: KnhZWCTA4cmshSZwt4sdogMVCfCWp1NlweKjsn7gNMegtMalRZ",
            "useQueryString: true"]
    )
    @GET("define")
    fun getMeanings(
        @Query("term") term: String
    ): Single<MeaningList>

}
