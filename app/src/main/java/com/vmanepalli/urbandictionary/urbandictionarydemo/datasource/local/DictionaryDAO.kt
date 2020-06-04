package com.vmanepalli.urbandictionary.urbandictionarydemo.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning

@Dao
interface DictionaryDAO {

    // So far pulling exact match and not looking for similar words when user scrolls to the bottom of recyclerview.
    // A future implementation is to add pagination
    @Query("SELECT * FROM meaning WHERE word LIKE :term ")
    fun findByTerm(term: String): List<Meaning>

    // Replacing old entries to make sure we get changes like more thumbs up/down, sound links and so on.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(meanings: List<Meaning>)

}