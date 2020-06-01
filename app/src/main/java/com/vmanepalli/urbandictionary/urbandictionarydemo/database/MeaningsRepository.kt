package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import android.app.Application
import com.vmanepalli.urbandictionary.urbandictionarydemo.isConnectedToInternet
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast
import io.reactivex.Observable

/**
 * @author vmanepalli
 * Date: 2020-05-31
 * Time: 16:52
 */
class MeaningsRepository(private val application: Application) {

    private val localRepository = MeaningsLocalRepository().invoke(application)
    private val remoteRepository = MeaningsRemoteRepository()

    fun getMeanings(searchTerm: String, completion: () -> Unit): Observable<List<Meaning>> {
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
        return localRepository.getMeanings(searchTerm, completion)
    }

}