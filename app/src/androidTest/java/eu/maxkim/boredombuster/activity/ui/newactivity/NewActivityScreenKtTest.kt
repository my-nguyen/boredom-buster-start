package eu.maxkim.boredombuster.activity.ui.newactivity

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.R
import eu.maxkim.boredombuster.Tags
import eu.maxkim.boredombuster.activity.androidActivity1
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class NewActivityScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // test that the activity name is displayed correctly on the card
    @Test
    fun activityNameDisplayedOnACard() {
        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = androidActivity1,
                isFavorite = false,
                onFavoriteClick = { },
                onLinkClick = { }
            )
        }

        composeTestRule.onNodeWithText(androidActivity1.name)
            .assertIsDisplayed()
    }

    // test that the favorite button triggers the onFavoriteClick function
    @Test
    fun onFavoriteClickCallbackIsTriggered() {
        val onFavoriteClick: (isFavorite: Boolean) -> Unit = mock()
        val isFavorite = false

        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = androidActivity1,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                onLinkClick = { }
            )
        }

        val contentDescription = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.cd_save_activity)

        composeTestRule.onNodeWithContentDescription(contentDescription)
            .performClick()

        verify(onFavoriteClick, times(1)).invoke(!isFavorite)
    }

    // test if clicking a link on the activity card triggers the onLinkClick callback by manually
    // adding a testTag to our component code and finding it in test
    @Test
    fun onLinkClickCallbackIsTriggered() {
        val onLinkClick: (link: String) -> Unit = mock()

        composeTestRule.setContent {
            NewActivityCard(
                modifier = Modifier.fillMaxWidth(),
                activity = androidActivity1,
                isFavorite = false,
                onFavoriteClick = { },
                onLinkClick = onLinkClick
            )
        }

        composeTestRule.onNodeWithTag(Tags.ActivityLink)
            .performClick()

        verify(onLinkClick, times(1)).invoke(androidActivity1.link)
    }

}
