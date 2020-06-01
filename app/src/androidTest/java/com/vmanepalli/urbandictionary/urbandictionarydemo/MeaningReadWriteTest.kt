package com.vmanepalli.urbandictionary.urbandictionarydemo

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.AppDatabase
import com.vmanepalli.urbandictionary.urbandictionarydemo.database.MeaningDAO
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MeaningReadWriteTest {
    private lateinit var meaningDAO: MeaningDAO
    private lateinit var db: AppDatabase

    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        meaningDAO = db.meaningDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadList() {
        val meaning = Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            3689,
            arrayListOf("Wat"),
            "watwat",
            "wat",
            3322419,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
        val list = listOf(meaning)
        meaningDAO.insertAll(list)
        val results = meaningDAO.findByTerm(meaning.word)
        MatcherAssert.assertThat(results.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(results[0].defid, CoreMatchers.equalTo(meaning.defid))
    }

    @Test
    @Throws(Exception::class)
    fun writeOneSoundURLAndCheck() {
        val meaning = Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            3689,
            arrayListOf("1"),
            "watwat",
            "wat",
            3322419,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
        val list = listOf(meaning)
        meaningDAO.insertAll(list)
        val results = meaningDAO.findByTerm(meaning.word)
        MatcherAssert.assertThat(results.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(results[0].defid, CoreMatchers.equalTo(meaning.defid))
        MatcherAssert.assertThat(results[0].sound_urls.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(results[0].sound_urls[0], CoreMatchers.equalTo("1"))
    }

    @Test
    @Throws(Exception::class)
    fun writeNoSoundURLAndCheck() {
        val meaning = Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            3689,
            arrayListOf(),
            "watwat",
            "wat",
            3322419,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
        val list = listOf(meaning)
        meaningDAO.insertAll(list)
        val results = meaningDAO.findByTerm(meaning.word)
        MatcherAssert.assertThat(results.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(results[0].defid, CoreMatchers.equalTo(meaning.defid))
        MatcherAssert.assertThat(results[0].sound_urls.size, CoreMatchers.equalTo(0))
    }

    @Test
    @Throws(Exception::class)
    fun writeMultipleSoundURLAndCheck() {
        val meaning = Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            3689,
            arrayListOf("1", "2", "3"),
            "watwat",
            "wat",
            3322419,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
        val list = listOf(meaning)
        meaningDAO.insertAll(list)
        val results = meaningDAO.findByTerm(meaning.word)
        MatcherAssert.assertThat(results.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(results[0].defid, CoreMatchers.equalTo(meaning.defid))
        MatcherAssert.assertThat(results[0].sound_urls.size, CoreMatchers.equalTo(3))
        MatcherAssert.assertThat(results[0].sound_urls[0], CoreMatchers.equalTo("1"))
        MatcherAssert.assertThat(results[0].sound_urls[1], CoreMatchers.equalTo("2"))
        MatcherAssert.assertThat(results[0].sound_urls[2], CoreMatchers.equalTo("3"))
    }
}