package info.zverev.ilya.rinewsreader.ui

import info.zverev.ilya.rinewsreader.models.ArticleData
import kotlinx.serialization.Serializable

@Serializable
object NewsListRoute

@Serializable
data class ArticleRoute(var title: String, val url: String)