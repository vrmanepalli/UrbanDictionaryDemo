package com.vmanepalli.urbandictionary.urbandictionarydemo.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable
import java.util.*

@Entity(tableName = "meaning", indices = [Index(value = ["defid"], unique = true), Index(value = ["word"], unique = false)])
data class Meaning(
    val definition: String, val permalink: String,
    val thumbs_up: Int,
    val sound_urls: MutableList<String> = arrayListOf(),
    val author: String,
    val word: String,
    @PrimaryKey
    val defid: Long,
    val current_vote: String,
    val written_on: String,
    val example: String,
    val thumbs_down: Int
) : Serializable

private const val SEPARATOR = ","

class SoundURLsConverter {
    @TypeConverter
    fun stringToList(value: String?): MutableList<String> {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList()
        }
        val sounds: MutableList<String>? = value?.split(SEPARATOR)?.toMutableList()
        return sounds ?: Collections.emptyList()
    }

    @TypeConverter
    fun listToString(cl: MutableList<String>?): String {
        var value = ""
        if (cl == null) {
            return value
        }
        for (sound in cl) {
            if (sound.isEmpty()) { continue }
            value += "$sound,"
        }
        if (value.isNotEmpty()) {
            value = value.substring(0,value.length-1)
        }
        return value
    }
}
