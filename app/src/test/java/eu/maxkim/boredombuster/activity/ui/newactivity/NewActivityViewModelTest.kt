package eu.maxkim.boredombuster.activity.ui.newactivity

import app.cash.turbine.test
import eu.maxkim.boredombuster.activity.fake.usecase.FakeDeleteActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeGetRandomActivity
import eu.maxkim.boredombuster.activity.fake.usecase.FakeIsActivitySaved
import eu.maxkim.boredombuster.activity.fake.usecase.FakeSaveActivity
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.fake.usecase.activity2
import eu.maxkim.boredombuster.util.CoroutineRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class NewActivityViewModelTest {
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

    // test that after an instance of NewActivityViewModel is created and all the coroutines have
    // successfully finished, the UI state is NewActivityUiState.Success
    @Test
    fun `creating a viewmodel updates ui state to success after loading`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        val expectedUiState = NewActivityUiState.Success(activity1, false)

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }

    // test that the UI state correctly updates to NewActivityUiState.Error if something goes wrong
    @Test
    fun `creating a viewmodel updates ui state to error in case of failure`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(isSuccessful = false), // our fake will return an error
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val currentState = viewModel.uiState.value
        assert(currentState is NewActivityUiState.Error)
    }

    // test that, if the activity is already saved, the UI state's isFavorite flag is set to true
    @Test
    fun `if activity is already saved, ui state's isFavorite is set to true`() {
        // Arrange
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(), // our fake will return an error
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved(isActivitySaved = true)
        )

        val expectedUiState = NewActivityUiState.Success(activity1, true)

        // Act
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }

    // test whether the new activity loads and replaces the current one when calling
    // loadNewActivity() explicitly
    @Test
    fun `calling loadNewActivity() updates ui state with a new activity`() {
        // Arrange
        val fakeGetRandomActivity = FakeGetRandomActivity()
        val viewModel = NewActivityViewModel(
            fakeGetRandomActivity,
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )
        // this can be omitted, but it is nice to not have any pending tasks
        coroutineRule.testDispatcher.scheduler.runCurrent()

        val expectedUiState = NewActivityUiState.Success(activity2, false)
        fakeGetRandomActivity.activity = activity2

        // Act
        viewModel.loadNewActivity()
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }

    // testing whether or not calling setIsFavorite(activity, true) method interacts with the
    // correct use case
    @Test
    fun `calling setIsFavorite(true) triggers SaveActivity use case`() {
        // Arrange
        val fakeSaveActivity = FakeSaveActivity()
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            fakeSaveActivity,
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        // Act
        viewModel.setIsFavorite(activity1, true)
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        assert(fakeSaveActivity.wasCalled)
    }

    // test for setIsFavorite(activity, false)
    @Test
    fun `calling setIsFavorite(false) triggers DeleteActivity use case`() {
        // Arrange
        val fakeDeleteActivity = FakeDeleteActivity()
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            FakeSaveActivity(),
            fakeDeleteActivity,
            FakeIsActivitySaved()
        )

        // Act
        viewModel.setIsFavorite(activity1, false)
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        assert(fakeDeleteActivity.wasCalled)
    }


    /**
     * Bonus turbine test
     */
    @Test
    fun `calling loadNewActivity() twice goes through expected ui states`() = runTest {
        val fakeGetRandomActivity = FakeGetRandomActivity()
        val viewModel = NewActivityViewModel(
            fakeGetRandomActivity,
            FakeSaveActivity(),
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        assert(viewModel.uiState.value is NewActivityUiState.Loading)

        launch {
            viewModel.uiState.test {
                with(awaitItem()) {
                    assert(this is NewActivityUiState.Success)
                    assertEquals((this as NewActivityUiState.Success).activity, activity1)
                }

                assert(awaitItem() is NewActivityUiState.Loading)

                with(awaitItem()) {
                    assert(this is NewActivityUiState.Success)
                    assertEquals((this as NewActivityUiState.Success).activity, activity2)
                }
                // this is not necessary for this specific test
                // but better safe than sorry
                // especially when dealing with hot flows
                cancelAndIgnoreRemainingEvents()
            }
        }

        // runs the initial loading
        runCurrent()

        // prepares and runs the second loading
        fakeGetRandomActivity.activity = activity2
        viewModel.loadNewActivity()
        runCurrent()
    }

    @Test
    fun `calling setIsFavorite(true) changes the ui state's isFavorite flag`() {
        // Arrange
        val fakeSaveActivity = FakeSaveActivity()
        val viewModel = NewActivityViewModel(
            FakeGetRandomActivity(),
            fakeSaveActivity,
            FakeDeleteActivity(),
            FakeIsActivitySaved()
        )

        val expectedUiState = NewActivityUiState.Success(activity1, true)

        // Act
        viewModel.setIsFavorite(activity1, true)
        coroutineRule.testDispatcher.scheduler.runCurrent()

        // Assert
        val actualState = viewModel.uiState.value
        assertEquals(actualState, expectedUiState)
    }
}