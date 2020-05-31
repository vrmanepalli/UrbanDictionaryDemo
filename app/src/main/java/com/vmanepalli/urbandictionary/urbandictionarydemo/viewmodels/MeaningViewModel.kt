package com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels

/**
 *
 * This view model is responsible for making DB calls and api calls to get old and new data.
 * As it gets new data from Urban Dictionary, it will save it to the database.
 *
 **/

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.MeaningsRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast

class MeaningViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MeaningViewModel(application) as T
    }
}

class MeaningViewModel(application: Application) : ViewModel() {

    private val meaningsRepository = MeaningsRepository().invoke(application)
    private val appContext = application
    private var searchTerm: String? = null

    fun getAllMeanings(): LiveData<List<Meaning>> {
        return meaningsRepository.getAllMeanings()
    }

    fun searchMeanings(
        connected: Boolean,
        searchTerm: String,
        completion: () -> Unit
    ) {
        this.searchTerm = searchTerm
        meaningsRepository.searchMeanings(connected, searchTerm, completion)
    }

    fun refresh(isConnected: Boolean, completion: () -> Unit) {
        if (!isConnected) {
            completion()
            appContext.toast("You are not connected to the internet! Loading from local storage, if any.")
            return
        }
        if (searchTerm == null) {
            appContext.toast("Refresh is unavailable when no word entry was made")
            completion()
            return
        }
        this.searchTerm?.let { searchMeanings(isConnected, it, completion) }
    }

}