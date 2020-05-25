package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning

@Dao
interface MeaningDAO {

    @Query("SELECT * FROM meaning WHERE word LIKE :term ")
    fun findByTerm(term: String): List<Meaning>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(meanings: List<Meaning>)

}