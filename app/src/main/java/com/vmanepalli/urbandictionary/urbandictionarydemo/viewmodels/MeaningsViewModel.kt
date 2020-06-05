package com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels

/**
 *
 * This view model is responsible for making DB calls and api calls to get old and new data.
 * As it gets new data from Urban Dictionary, it will save it to the database.
 *
 **/

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vmanepalli.urbandictionary.urbandictionarydemo.MeaningsAdapter
import com.vmanepalli.urbandictionary.urbandictionarydemo.ascendingOrder
import com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.DictionaryRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Suggestions
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MeaningViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MeaningViewModel(application) as T
    }
}

class MeaningViewModel(private val application: Application) : ViewModel() {

    //region Variable declaration
    private val meaningsRepository = DictionaryRepository(application)
    private lateinit var searchTerm: String
    val meanings = MutableLiveData<List<Meaning>>()
    val suggestions = MutableLiveData<List<Suggestions>>()

    val meaningsAdapter: MeaningsAdapter by lazy { MeaningsAdapter(listOf()) }
    //endregion

    //region Activity helper functions
    fun flipSort(): Boolean {
        if (isEmpty()) {
            // Nothing to sort, so toast a message and return
            application.toast("Sort is unavailable for empty data.")
            return application.ascendingOrder
        }
        val order = !application.ascendingOrder
        application.ascendingOrder = order
        meaningsAdapter.sortedBy(order)
        notifyDataChange()
        return order
    }

    fun refresh() {
        if (searchTerm.isEmpty()) {
            application.toast("Refresh is unavailable for empty search")
            return
        }
        searchMeanings(searchTerm)
    }

    fun searchMeanings(
        searchTerm: String
    ) {
        this.searchTerm = searchTerm
        meaningsRepository.getMeanings(searchTerm)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<List<Meaning>>() {
                override fun onComplete() {
                    notifyDataChange()
                }

                override fun onError(e: Throwable?) {
                    meanings.postValue(listOf())
                    e?.message?.let { application.toast(it) }
                }

                override fun onNext(value: List<Meaning>?) {
                    value?.let {
                        meanings.postValue(it)
                        replaceAdapterData(it)
                        meaningsAdapter.sortedBy(application.ascendingOrder)
                    }
                }
            })
    }

    fun searchSuggestions(searchTerm: String) {
        meaningsRepository.getSuggestions(searchTerm)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<List<Suggestions>>() {
                override fun onComplete() {
                    print("Done reading suggestions")
                }

                override fun onNext(value: List<Suggestions>?) {
                    value?.let { suggestions.postValue(it) }
                }

                override fun onError(e: Throwable?) {
                    e?.let { print(e.localizedMessage) }
                }

            })
    }
    //endregion

    //region Adapter helper functions
    fun replaceAdapterData(meanings: List<Meaning>) {
        with(meaningsAdapter) {
            replaceData(meanings, application.ascendingOrder)
        }
    }

    fun notifyDataChange() {
        GlobalScope.launch(Dispatchers.Main.immediate) { meaningsAdapter.notifyDataSetChanged() }
    }

    private fun isEmpty(): Boolean {
        return meanings.value?.isEmpty() ?: true
    }
    // endregion

}