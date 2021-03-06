package eu.szwiec.checkittravelkit.ui.search

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import eu.szwiec.checkittravelkit.R
import eu.szwiec.checkittravelkit.prefs.Preferences
import eu.szwiec.checkittravelkit.repository.CountryRepository
import eu.szwiec.checkittravelkit.util.NonNullLiveData
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.AutoCloseKoinTest
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SearchViewModelTest : AutoCloseKoinTest() {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val context: Context by inject()
    private val preferences = mock<Preferences>()
    private val repository = mock<CountryRepository>()
    private val vm = SearchViewModel(context, preferences, repository)

    @Test
    fun initStateWhenEmptyOrigin() {
        whenever(preferences.origin).thenReturn("")
        vm.initState()

        vm.state.value shouldEqual State.ChooseOrigin
    }

    @Test
    fun initStateWhenOriginExistis() {
        whenever(preferences.origin).thenReturn("Poland")
        vm.initState()

        vm.state.value shouldEqual State.ChooseDestination
        vm.origin.value shouldEqual preferences.origin
    }

    @Test
    fun setState() {
        vm.setState(State.ChooseOrigin)
        vm.originHint.value shouldEqual context.getString(R.string.where_are_you_from)
        vm.state.value shouldEqual State.ChooseOrigin

        vm.setState(State.ChooseDestination)
        vm.originHint.value shouldEqual ""

        vm.setState(State.ShowInfo("Poland"))
        vm.destination.value shouldEqual ""
    }

    @Test
    fun submitOrigin() {
        whenever(repository.getCountryNames()).thenReturn(NonNullLiveData(listOf("Poland", "Germany")))
        val vm = SearchViewModel(context, preferences, repository)
        vm.state.value = State.ChooseOrigin

        vm.origin.value = "Pol"
        vm.submitOrigin()
        vm.state.value shouldEqual State.ChooseOrigin

        vm.origin.value = "Poland"
        vm.submitOrigin()
        vm.state.value shouldEqual State.ChooseDestination
    }

    @Test
    fun submitDestination() {
        whenever(repository.getCountryNames()).thenReturn(NonNullLiveData(listOf("Poland", "Germany")))
        val vm = SearchViewModel(context, preferences, repository)
        vm.state.value = State.ChooseDestination

        vm.destination.value = "Ger"
        vm.submitDestination()
        vm.state.value shouldEqual State.ChooseDestination

        vm.destination.value = "Germany"
        vm.submitDestination()
        vm.state.value.shouldBeInstanceOf(State.ShowInfo::class)
    }
}