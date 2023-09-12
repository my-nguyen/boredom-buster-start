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

}
