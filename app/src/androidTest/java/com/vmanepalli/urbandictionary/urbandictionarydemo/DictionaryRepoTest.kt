package com.vmanepalli.urbandictionary.urbandictionarydemo

import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.AppDatabase
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.DictionaryRepository
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.MeaningDAO
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author vmanepalli
 * Date: 2020-05-28
 * Time: 06:26
 */
@RunWith(AndroidJUnit4::class)
class DictionaryRepoTest {
    private lateinit var repo: DictionaryRepository
    private lateinit var mockMeaningDAO: MeaningDAO
    private lateinit var meanings: MutableLiveData<List<Meaning>>

    private val lock: CountDownLatch = CountDownLatch(1)

    @After
    fun tearDown() {

    }

    @Test
    fun insertMeaningsTest() {
        val meaning = Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            3,
            arrayListOf("1", "2", "3"),
            "watwat",
            "wat",
            3,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val appDB = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        mockMeaningDAO = appDB.meaningDAO()
        meanings = MutableLiveData<List<Meaning>>()
        repo = mock<DictionaryRepository> {
            on { meaningDAO } doReturn mockMeaningDAO
            on { getAllMeanings() }
        }

        repo.insertMeanings(arrayListOf(meaning), "wat")
        lock.await(2000, TimeUnit.MILLISECONDS)
        verify(mockMeaningDAO).findByTerm("wat")
    }
}