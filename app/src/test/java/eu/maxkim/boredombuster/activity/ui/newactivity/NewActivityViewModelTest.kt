package eu.maxkim.boredombuster.activity.ui.newactivity

import eu.maxkim.boredombuster.activity.fake.usecase.FakeDeleteActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeGetRandomActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeIsActivitySaved
import eu.maxkim.boredombuster.activity.fake.usecase.FakeSaveActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class NewActivityViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `creating a viewmodel exposes loading ui state`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // Assert
        assert(viewModel.uiState.value is NewActivityUiState.Loading)
    }
}