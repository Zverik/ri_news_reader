package info.zverev.ilya.rinewsreader.io

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.zverev.ilya.rinewsreader.BuildConfig
import info.zverev.ilya.rinewsreader.models.ArticleData
import info.zverev.ilya.rinewsreader.models.NewsApiResponse
import info.zverev.ilya.rinewsreader.models.articleDataOrNullOf
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

data class ArticleFeedState(
    val isLoading: Boolean = true,
    val articles: List<ArticleData> = emptyList(),
    val error: String? = null,
)

const val PAGE_SIZE = 23  // 21 in reality lol wtf
const val COUNTRY_CODE = "us"  // other countries return zero results

typealias ErrorCallback = (String) -> Unit

class ArticleFeedViewModel : ViewModel() {
    private val _uiState = mutableStateOf(ArticleFeedState())
    val uiState: State<ArticleFeedState> = _uiState

    private var currentPage = 0
    private var isLoadingMore = false
    private var haveMore = true

    private val TAG = "ArticleFeedViewModel"

    private val client = OkHttpClient()
    private val json = Json

    init {
        loadArticles()
    }

    fun loadArticles(informError: ErrorCallback? = null) {
        if (!haveMore) return
        if (isLoadingMore) return
        isLoadingMore = true

        viewModelScope.launch {
            val url = "https://newsapi.org/v2/top-headlines".toHttpUrl().newBuilder()
                .addQueryParameter("country", COUNTRY_CODE)
                .addQueryParameter("pageSize", PAGE_SIZE.toString())
                .addQueryParameter("page", currentPage.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .header("X-Api-Key", BuildConfig.NEWSAPI_KEY)
                .build()

            Log.i(TAG, "Requesting $url")
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    var error: String? = null
                    var articles = _uiState.value.articles

                    if (!response.isSuccessful) {
                        error = "NewsAPI returned error ${response.code}"
                        response.close()
                    } else {
                        val body = response.body?.string()
                        if (body == null) {
                            error = "Empty body received from News API"
                        } else try {
                            val data = json.decodeFromString<NewsApiResponse>(body)
                            if (data.articles.isEmpty()) haveMore = false
                            articles =
                                articles.plus(data.articles.mapNotNull { articleDataOrNullOf(it) })
                        } catch (e: Exception) {
                            error = "Error decoding NewsAPI response: $e"
                        }
                    }

                    Log.i(TAG, "Request complete, error=$error, articles ${articles.size}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error,
                        articles = articles,
                    )

                    isLoadingMore = false
                    if (error == null) currentPage++

                    if (error != null && informError != null) {
                        informError(error)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    val error = "Error loading data from News API: $e"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error,
                    )
                    isLoadingMore = false
                    if (informError != null) informError(error)
                }
            })
        }
    }
}