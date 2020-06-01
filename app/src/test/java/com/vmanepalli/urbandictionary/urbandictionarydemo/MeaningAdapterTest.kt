package com.vmanepalli.urbandictionary.urbandictionarydemo

import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test

class MeaningAdapterTest {
    @Test
    fun testSortInAscendingOrder() {
        val meaning1 = getMeaning(1)
        val meaning2 = getMeaning(2)
        val meaning3 = getMeaning(3)
        val adapter = MeaningsAdapter(arrayListOf(meaning2, meaning3, meaning1))
        Assert.assertNotNull(adapter)
        MatcherAssert.assertThat(adapter.itemCount, CoreMatchers.equalTo(3))
        adapter.sortedBy(true)
        MatcherAssert.assertThat(adapter.meanings[0].defid, CoreMatchers.equalTo(meaning1.defid))
        MatcherAssert.assertThat(adapter.meanings[1].defid, CoreMatchers.equalTo(meaning2.defid))
        MatcherAssert.assertThat(adapter.meanings[2].defid, CoreMatchers.equalTo(meaning3.defid))
    }

    @Test
    fun testSortInDescendingOrder() {
        val meaning1 = getMeaning(1)
        val meaning2 = getMeaning(2)
        val meaning3 = getMeaning(3)
        val adapter = MeaningsAdapter(arrayListOf(meaning2, meaning3, meaning1))
        Assert.assertNotNull(adapter)
        MatcherAssert.assertThat(adapter.itemCount, CoreMatchers.equalTo(3))
        adapter.sortedBy(false)
        MatcherAssert.assertThat(adapter.meanings[0].defid, CoreMatchers.equalTo(meaning3.defid))
        MatcherAssert.assertThat(adapter.meanings[1].defid, CoreMatchers.equalTo(meaning2.defid))
        MatcherAssert.assertThat(adapter.meanings[2].defid, CoreMatchers.equalTo(meaning1.defid))
    }

    private fun getMeaning(thumbsUp: Int): Meaning {
        return Meaning(
            "The only [proper] [response] to something that makes absolutely [no sense].",
            "http://wat.urbanup.com/3322419",
            thumbsUp,
            arrayListOf("1", "2", "3"),
            "watwat",
            "wat",
            2,
            "",
            "2008-09-04T00:00:00.000Z",
            "1: If all the animals on the [equator] were capable of [flattery], Halloween and Easter would fall on the same day. 2: wat 1: Wow your cock is almost as big as my dad's. 2: wat 1: I accidentially a whole [coke bottle] 2: You accidentially what? 1: A whole coke bottle 2: wat",
            426
        )
    }
}