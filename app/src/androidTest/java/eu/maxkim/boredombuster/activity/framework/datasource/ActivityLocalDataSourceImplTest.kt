package eu.maxkim.boredombuster.activity.framework.datasource

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import eu.maxkim.boredombuster.activity.framework.db.ActivityDao
import eu.maxkim.boredombuster.framework.AppDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ActivityLocalDataSourceImplTest {
    private lateinit var activityDao: ActivityDao
    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // build an in-memory version of the database, so we don't have to create a real database
        // every time we run a test
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        activityDao = database.activityDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    // test if we can save an activity to the database and access it.
    @Test
    fun canSaveActivityToTheDbAndReadIt() = runTest {
        // Arrange
        val activityLocalDataSource = ActivityLocalDataSourceImpl(activityDao)

        // Act
        activityLocalDataSource.saveActivity(androidActivity1)

        // Assert
        assert(activityLocalDataSource.isActivitySaved(androidActivity1.key))
    }
}
