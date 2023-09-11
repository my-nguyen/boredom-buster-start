package eu.maxkim.boredombuster.activity.ui.newactivity

import eu.maxkim.boredombuster.activity.fake.usecase.FakeDeleteActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeGetRandomActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeIsActivitySaved
import eu.maxkim.boredombuster.activity.fake.usecase.FakeSaveActivity
import eu.maxkim.boredombuster.util.CoroutineRule
import org.junit.Rule
import org.junit.Test

internal class NewActivityViewModelTest {
    @get:Rule
    val coroutineRule = CoroutineRule()

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