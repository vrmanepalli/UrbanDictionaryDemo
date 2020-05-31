package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import android.app.Application
import android.util.Log
import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vmanepalli.urbandictionary.urbandictionarydemo.api.MeaningsRetriever
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.MeaningList
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeaningsRepository {

    lateinit var meaningDAO: MeaningDAO
    private lateinit var application: Application
    private var meanings: MutableLiveData<List<Meaning>> = MutableLiveData<List<Meaning>>()
    private var meaningsRetriever = MeaningsRetriever()

    operator fun invoke(@NonNull application: Application) = run {
        this.also {
            this.application = application
            val db = AppDatabase(application)
            meaningDAO = db.meaningDAO()
        }
    }

    fun getAllMeanings(): LiveData<List<Meaning>> {
        return meanings
    }

    // Inserting to DB which means also change in DB, so posting results
    fun insertMeanings(results: List<Meaning>?, forTerm: String) {
        GlobalScope.launch(Dispatchers.Default) {
            if (results != null) {
                meaningDAO.insertAll(results)
            }
            searchMeaningsOnDB(forTerm)
        }
    }

    // Posting DB queries results so the observer in SearchableActivity or anywhere will update UI.
    fun searchMeanings(isConnected: Boolean, forTerm: String, completion: () -> Unit) {
        searchMeaningsOnDB(forTerm)
        if (!isConnected) {
            completion()
            application.toast("You are not connected to the internet! Loading from local storage, if any.")
            return
        }
        searchMeaningsOnline(forTerm, completion)
    }

    private fun searchMeaningsOnDB(forTerm: String) {
        GlobalScope.launch(Dispatchers.Default) {
            meanings.postValue(meaningDAO.findByTerm(forTerm))
        }
    }

    fun searchMeaningsOnline(searchTerm: String, completion: () -> Unit) {
        val callback = object : Callback<MeaningList> {
            override fun onFailure(call: Call<MeaningList>, t: Throwable) {
                Log.e("SearchableActivity", "Problems calling API", t)
                completion()
                application.toast("Problems calling API!")
            }

            override fun onResponse(call: Call<MeaningList>, response: Response<MeaningList>) {
                completion()
                response.isSuccessful.let {
                    val newMeanings = response.body()?.list
                    if (newMeanings?.size ?: 0 == 0) {
                        application.toast("No match was found!")
                    }
                    insertMeanings(newMeanings, searchTerm)
                }
            }
        }
        meaningsRetriever.getMeanings(callback, searchTerm)
    }

}