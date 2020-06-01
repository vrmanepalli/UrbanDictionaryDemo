package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import android.app.Application
import androidx.annotation.NonNull
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MeaningsLocalRepository {

    private lateinit var meaningDAO: MeaningDAO
    private lateinit var application: Application

    operator fun invoke(@NonNull application: Application) = run {
        this.also {
            this.application = application
            val db = AppDatabase(application)
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
    fun getMeanings(forTerm: String, completion: () -> Unit): Observable<List<Meaning>> {
        return Observable.fromCallable {
            completion()
            meaningDAO.findByTerm(forTerm)
        }
    }


}