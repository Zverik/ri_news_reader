package info.zverev.ilya.rinewsreader.models

import info.zverev.ilya.rinewsreader.util.UrlSerializer
import kotlinx.serialization.Serializable
import java.net.URL

@Serializable
data class ArticleData(
    val title: String,
    val author: String?,
    val description: String,
    val content: String?,
    val published: kotlinx.datetime.Instant,
    val source: String,
    @Serializable(UrlSerializer::class)
    val articleUrl: URL,
    @Serializable(UrlSerializer::class)
    val imageUrl: URL?,
)