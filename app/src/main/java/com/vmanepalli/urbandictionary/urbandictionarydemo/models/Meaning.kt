package com.vmanepalli.urbandictionary.urbandictionarydemo.models

import java.io.Serializable

data class Meaning(
    val definition: String, val permalink: String,
    val thumbs_up: Int,
    val sound_urls: MutableList<String> = arrayListOf(),
    val author: String,
    val word: String,
    val defid: Long,
    val current_vote: String,
    val written_on: String,
    val example: String,
    val thumbs_down: Int
) : Serializable
