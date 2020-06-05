package com.vmanepalli.urbandictionary.urbandictionarydemo.datasource

import android.app.Application
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.api.DictionaryRemoteRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.local.DictionaryLocalRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.isConnectedToInternet
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Suggestions
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast
import io.reactivex.Observable

/**
 * @author vmanepalli
 * Date: 2020-05-31
 * Time: 16:52
 */
class DictionaryRepository(private val application: Application) {

    private val localRepository = DictionaryLocalRepository()
        .invoke(application)
    private val remoteRepository =
        DictionaryRemoteRepository()

    fun getMeanings(searchTerm: String): Observable<List<Meaning>> {
        application.isConnectedToInternet?.let {
            if (it) {
                return remoteRepository.getMeanings(searchTerm)
                    .flatMap {
                        return@flatMap localRepository.insertMeanings(it).toSingleDefault(it)
                            .toObservable()
                    }
            } else {
                application.toast("You are not connected to the internet! Loading from local storage, if any.")
            }
        }
        return localRepository.getMeanings(searchTerm)
    }

    fun getSuggestions(searchTerm: String): Observable<List<Suggestions>> {
        return localRepository.getSuggestions(searchTerm)
    }

}