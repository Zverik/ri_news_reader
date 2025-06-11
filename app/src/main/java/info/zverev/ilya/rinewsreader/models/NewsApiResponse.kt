package info.zverev.ilya.rinewsreader.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.net.URI
import java.time.ZonedDateTime

@Serializable
data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsApiArticle>,
)

@Serializable
data class NewsApiArticle(
    val author: String?,
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val source: NewsApiSource,
)

@Serializable
data class NewsApiSource(
    val id: String?,
    val name: String,
)

fun articleDataOf(article: NewsApiArticle) = ArticleData(
    title = article.title,
    author = article.author,
    description = article.description ?: "",
    content = article.content,
    published = Instant.parse(article.publishedAt),
    source = article.source.name,
    articleUrl = URI(article.url).toURL(),
    imageUrl = URI(article.urlToImage).toURL(),
)

fun articleDataOrNullOf(article: NewsApiArticle): ArticleData? {
    return try {
        articleDataOf(article)
    } catch (e: Exception) {
        null
    }
}