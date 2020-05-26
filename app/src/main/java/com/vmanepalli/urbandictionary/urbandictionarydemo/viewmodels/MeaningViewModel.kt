package com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels

/**
 *
 * This view model is responsible for making DB calls and api calls to get old and new data.
 * As it gets new data from Urban Dictionary, it will save it to the database.
 *
 **/

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.api.MeaningsRetriever
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.DictionaryRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.MeaningList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeaningViewModelFactory(private val application: Application, private val term: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MeaningViewModel(application, term) as T
    }
}

class MeaningViewModel(application: Application, term: String) : ViewModel() {

    private val context: Application = application
    private val dictionaryRepository: DictionaryRepository = DictionaryRepository()
        .invoke(application, term)
    private val meaningsRetriever: MeaningsRetriever = MeaningsRetriever()

    fun getAllMeanings(): LiveData<List<Meaning>> {
        return dictionaryRepository.getAllMeanings()
    }

    fun searchMeaning(isConnected: Boolean, searchTerm: String) {
        // Search in DB
        filterAllMeanings(searchTerm)
        // Search via API call
        searchMeaningsOnline(isConnected, searchTerm)
    }

    fun searchMeaningsOnline(isConnected: Boolean, searchTerm: String) {
        if (!isConnected) {
            Toast.makeText(
                context,
                "You are not connected to the internet! Loading from local storage, if any.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val callback = object : Callback<MeaningList> {
            override fun onFailure(call: Call<MeaningList>, t: Throwable) {
                Log.e("MainActivity", "Problems calling API", t)
                if (isConnected) {
                    Toast.makeText(context, "Problems calling API!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        context,
                        "You are not connected to the internet! Loading from local storage, if any.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call<MeaningList>, response: Response<MeaningList>) {
                response.isSuccessful.let {
                    val newMeanings = response.body()?.list
                    if (newMeanings?.size ?: 0 == 0) {
                        Toast.makeText(context, "No match was found!", Toast.LENGTH_LONG).show()
                    }
                    dictionaryRepository.insertMeanings(newMeanings, searchTerm)
                }
            }
        }
        meaningsRetriever.getMeanings(callback, searchTerm)
    }

    // Search in DB my placing query for term
    private fun filterAllMeanings(term: String) {
        dictionaryRepository.filterAllMeanings(term)
    }

}