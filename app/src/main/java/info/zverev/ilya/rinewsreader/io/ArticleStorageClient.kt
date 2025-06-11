package info.zverev.ilya.rinewsreader.io

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import info.zverev.ilya.ArticleStorageGrpcKt
import info.zverev.ilya.article
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.Closeable
import java.net.URI

class ArticleStorageClient(uri: URI) : Closeable {
    val responseState = mutableStateOf("")

    private val channel = let {
        Log.i("ArticleStorageClient", "Connecting to $uri")

        val builder = ManagedChannelBuilder.forAddress(uri.host, uri.port)
        if (uri.scheme == "https") {
            builder.useTransportSecurity()
        } else {
            builder.usePlaintext()
        }

        builder.executor(Dispatchers.IO.asExecutor()).build()
    }

    private val service = ArticleStorageGrpcKt.ArticleStorageCoroutineStub(channel)

    suspend fun saveArticle(title: String, url: String) {
        try {
            val request = article {
                this.title = title
                this.url = url
            }
            val response = service.saveArticle(request)
            responseState.value = response.error
        } catch (e: Exception) {
            responseState.value = e.message ?: "Unknown Error"
        }
        Log.i("ArticleStorageClient", "Saving result: ${responseState.value}")
    }

    override fun close() {
        channel.shutdownNow()
    }
}