package com.vmanepalli.urbandictionary.urbandictionarydemo.database

import android.content.Context
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.SoundURLsConverter

@Database(
    entities = [Meaning::class],
    version = 1
)
@TypeConverters(SoundURLsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun meaningDAO(): MeaningDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(@NonNull context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    )
                        .also { instance = it }
            }

        private fun buildDatabase(@NonNull context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dictionary.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    }

}