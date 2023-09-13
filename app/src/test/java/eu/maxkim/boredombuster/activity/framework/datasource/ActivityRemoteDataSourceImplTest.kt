package eu.maxkim.boredombuster.activity.framework.datasource

import com.squareup.moshi.Moshi
import eu.maxkim.boredombuster.activity.framework.api.ActivityApiClient
import eu.maxkim.boredombuster.activity.framework.api.ActivityTypeAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@ExperimentalCoroutinesApi
class ActivityRemoteDataSourceImplTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: ActivityApiClient

    private val client = OkHttpClient.Builder().build()

    private val moshi: Moshi = Moshi.Builder()
        .add(ActivityTypeAdapter())
        .build()

    @Before
    fun createServer() {
        mockWebServer = MockWebServer()

        apiClient = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // setting a dummy url
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
            .create(ActivityApiClient::class.java)
    }

    @After
    fun shutdownServer() {
        mockWebServer.shutdown()
    }
}
