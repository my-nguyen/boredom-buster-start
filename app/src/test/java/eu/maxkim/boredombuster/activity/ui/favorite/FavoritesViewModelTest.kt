package eu.maxkim.boredombuster.activity.ui.favorite

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dagger.Component
import eu.maxkim.boredombuster.activity.fake.usecase.activity1
import eu.maxkim.boredombuster.activity.fake.usecase.activity2
import eu.maxkim.boredombuster.activity.model.Activity
import eu.maxkim.boredombuster.activity.usecase.DeleteActivity
import eu.maxkim.boredombuster.activity.usecase.GetFavoriteActivities
import eu.maxkim.boredombuster.util.CoroutineRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class FavoritesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockGetFavoriteActivities: GetFavoriteActivities = mock()
    private val mockDeleteActivity: DeleteActivity = mock()

    // MockitoJUnit.rule() and Component are necessary for Mockito annotation
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    @Mock
    private lateinit var mockComponent: Component

    // an Observer mock
    private val activityListObserver: Observer<FavoritesUiState> = mock()
    // ArgumentCaptor observes and captures a LiveData
    // the @Captor annotation requires MockitoJUnit.rule() and Component above
    @Captor
    private lateinit var activityListCaptor: ArgumentCaptor<FavoritesUiState>

    // need to set the Dispatcher.Main
    @get:Rule
    val coroutineRule = CoroutineRule()

    @Test
    fun `the view model maps list of activities to list ui state`() {
        // Arrange
        val liveDataToReturn = MutableLiveData<List<Activity>>()
            .apply { value = listOf(activity1, activity2) }

        val expectedList = listOf(activity1, activity2)

        whenever(mockGetFavoriteActivities.invoke()).doReturn(liveDataToReturn)

        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        viewModel.uiStateLiveData.observeForever(activityListObserver)

        // Assert
        // verify that our mock Observer’s onChanged method is called once, and use the
        // activityListCaptor to capture the passed value
        verify(activityListObserver, times(1)).onChanged(activityListCaptor.capture())
        // access the activityListCaptor.value
        assert(activityListCaptor.value is FavoritesUiState.List)

        val actualList = (activityListCaptor.value as FavoritesUiState.List).activityList
        assertEquals(actualList, expectedList)
    }

    // similar to the first mock test, but an empty list is returned from the mock's LiveData and
    // we assert that the FavoritesUiState is Empty.
    @Test
    fun `the view model maps empty list of activities to empty ui state`() {
        // Arrange
        val liveDataToReturn = MutableLiveData<List<Activity>>()
            .apply { value = listOf() }

        whenever(mockGetFavoriteActivities.invoke()).doReturn(liveDataToReturn)

        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        viewModel.uiStateLiveData.observeForever(activityListObserver)

        // Assert
        verify(activityListObserver, times(1)).onChanged(activityListCaptor.capture())
        assert(activityListCaptor.value is FavoritesUiState.Empty)
    }

    // test whether calling deleteActivity() interacts with the correct use case
    // runTest() is required to call the coroutine DeleteActivity.invoke() (runBlockingTest is deprecated)
    @Test
    fun `calling deleteActivity() interacts with the correct use case`() = runTest {
        // Arrange
        val viewModel = FavoritesViewModel(
            mockGetFavoriteActivities,
            mockDeleteActivity
        )

        // Act
        // deleteActivity() launches a coroutine in the viewModelScope, and we need to set the Dispatcher.Main
        viewModel.deleteActivity(activity1)
        // call advanceUntilIdle() or runCurrent() after deleteActivity() to make sure the launched
        // coroutine completes its job
        advanceUntilIdle() // works the same as runCurrent() in this case

        // Assert
        verify(mockDeleteActivity, times(1)).invoke(activity1)
    }
}
