package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DictionaryRepository {

    private lateinit var meaningDAO: MeaningDAO
    private var meanings: MutableLiveData<List<Meaning>> = MutableLiveData<List<Meaning>>()

    operator fun invoke(@NonNull application: Application, term: String) = run {
        this.also {
            val db = AppDatabase(application)
            meaningDAO = db.meaningDAO()
            filterAllMeanings(term)
        }
    }

    // Inserting to DB which means also change in DB, so posting results
    fun insertMeanings(results: List<Meaning>?, forTerm: String) {
        GlobalScope.launch(Dispatchers.Default) {
            if (results != null) {
                meaningDAO.insertAll(results)
            }
            // Posting DB queries results so the observer in MainActivity or anywhere will update UI.
            meanings.postValue(meaningDAO.findByTerm(forTerm))
        }
    }

    // Posting DB queries results so the observer in MainActivity or anywhere will update UI.
    fun filterAllMeanings(forTerm: String) {
        GlobalScope.launch(Dispatchers.Default) {
            meanings.postValue(meaningDAO.findByTerm(forTerm))
        }
    }

    fun getAllMeanings(): LiveData<List<Meaning>> {
        return meanings
    }

}