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
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.MeaningsRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.toast
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MeaningViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MeaningViewModel(application) as T
    }
}

class MeaningViewModel(private val application: Application) : ViewModel() {

    //region Variable declaration
    private val meaningsRepository = MeaningsRepository(application)
    private var searchTerm: String = ""
    private val meanings = MutableLiveData<List<Meaning>>()

    val meaningsAdapter: MeaningsAdapter by lazy { MeaningsAdapter(listOf()) }
    //endregion

    //region Repository helper functions
    fun getAllMeanings(): LiveData<List<Meaning>> {
        return meanings
    }

    fun searchMeanings(
        searchTerm: String,
        completion: () -> Unit
    ) {
        this.searchTerm = searchTerm
        meaningsRepository.getMeanings(searchTerm, completion)
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<List<Meaning>>() {
                override fun onComplete() {
                    completion()
                }

                override fun onError(e: Throwable?) {
                    completion()
                    e?.message?.let { application.toast(it) }
                }

                override fun onNext(value: List<Meaning>?) {
                    value?.let {
                        meanings.postValue(it)
                    }
                }
            })
    }

    fun refresh(completion: () -> Unit) {
        if (searchTerm.isEmpty()) {
            application.toast("Refresh is unavailable for empty search")
            completion()
            return
        }
        searchMeanings(searchTerm, completion)
    }
    //endregion

    //region Adapter helper functions
    fun replaceAdapterData(meanings: List<Meaning>) {
        with(meaningsAdapter) {
            replaceData(meanings, application.ascendingOrder)
        }
    }

    fun notifyDataChange() {
        meaningsAdapter.notifyDataSetChanged()
    }

    fun sortMeanings(): Boolean {
        if (isEmpty()) {
            // Nothing to sort, so toast a message and return
            application.toast("Sort is unavailable for empty data.")
            return application.ascendingOrder
        }
        val order = !application.ascendingOrder
        application.ascendingOrder = order
        this.sortedBy(order)
        return order
    }
    //endregion

    // region Private helper functions
    private fun sortedBy(ascendingOrder: Boolean) {
        with(meaningsAdapter) {
            sortedBy(ascendingOrder)
            notifyDataSetChanged()
        }
    }

    private fun isEmpty(): Boolean {
        return meanings.value?.isEmpty() ?: true
    }
    // endregion

}