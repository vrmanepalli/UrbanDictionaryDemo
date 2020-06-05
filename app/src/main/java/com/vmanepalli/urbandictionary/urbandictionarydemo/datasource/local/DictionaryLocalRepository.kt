package com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.local

import android.app.Application
import androidx.annotation.NonNull
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Suggestions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DictionaryLocalRepository {

    private lateinit var meaningDAO: DictionaryDAO
    private lateinit var application: Application

    operator fun invoke(@NonNull application: Application) = run {
        this.also {
            this.application = application
            val db =
                AppDatabase(
                    application
                )
            meaningDAO = db.meaningDAO()
        }
    }

    // Inserting to DB which means also change in DB, so posting results
    fun insertMeanings(results: List<Meaning>?): Completable {
        GlobalScope.launch(Dispatchers.Default) {
            results?.let {
                meaningDAO.insertAll(it)
            }
        }
        return Single.just(1).toCompletable()
    }

    // Posting DB queries results so the observer in SearchableActivity or anywhere will update UI.
    fun getMeanings(forTerm: String): Observable<List<Meaning>> {
        return Observable.fromCallable {
            meaningDAO.findByTerm(forTerm)
        }
    }

    fun getSuggestions(forTerm: String): Observable<List<Suggestions>> {
        return  Observable.fromCallable{
            meaningDAO.findSuggestions(forTerm)
        }
    }


}