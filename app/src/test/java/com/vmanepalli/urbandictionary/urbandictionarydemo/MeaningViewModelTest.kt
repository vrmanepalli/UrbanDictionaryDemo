package com.vmanepalli.urbandictionary.urbandictionarydemo

import android.app.Application
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import com.vmanepalli.urbandictionary.urbandictionarydemo.viewmodels.MeaningViewModel
import io.mockk.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author vmanepalli
 * Date: 2020-05-31
 * Time: 22:04
 */

@RunWith(MockitoJUnitRunner::class)
class MeaningViewModelTest {

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @After
    fun afterTests() {
        unmockkAll()
        // or unmockkObject(MockObj)
    }

    @Test
    fun testEmptyListToStartWith() {
        val applicationMock = mockk<Application>(relaxed = true)
        every { applicationMock.ascendingOrder } returns true
        val viewModel = MeaningViewModel(applicationMock)
        assertThat(viewModel.meaningsAdapter.itemCount, equalTo(0))
    }

    @Test
    fun testReplaceAdapterData() {
        val applicationMock = mockk<Application>(relaxed = true)
        every { applicationMock.ascendingOrder } returns true

        val viewModel = MeaningViewModel(applicationMock)

        val meanings = getMeaning(1)
        viewModel.replaceAdapterData(listOf(meanings))

        assertThat(viewModel.meaningsAdapter.meanings, equalTo(listOf(meanings)))
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