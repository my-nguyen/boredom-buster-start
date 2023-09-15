package eu.maxkim.boredombuster.ui

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule

@HiltAndroidTest
class BoredomBusterAppTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)
}
