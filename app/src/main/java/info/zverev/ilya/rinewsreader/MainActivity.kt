package info.zverev.ilya.rinewsreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import info.zverev.ilya.rinewsreader.io.ArticleStorageClient
import info.zverev.ilya.rinewsreader.ui.ArticleRoute
import info.zverev.ilya.rinewsreader.ui.ArticleScreen
import info.zverev.ilya.rinewsreader.ui.NewsListRoute
import info.zverev.ilya.rinewsreader.ui.NewsListScreen
import info.zverev.ilya.rinewsreader.ui.theme.RiNewsReaderTheme
import java.net.URI

class MainActivity : ComponentActivity() {
    private val serverUri by lazy { URI(BuildConfig.GRPC_SERVER) }
    val articleService by lazy { ArticleStorageClient(serverUri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RiNewsReaderTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NewsListRoute
                ) {
                    composable<NewsListRoute> { NewsListScreen(
                        onRoute = { navController.navigate(it) }
                    ) }
                    composable<ArticleRoute> { stack ->
                        val route: ArticleRoute = stack.toRoute()
                        ArticleScreen(
                            title = route.title,
                            url = route.url,
                            navigateBack = { navController.navigateUp() },
                            articleService = articleService,
                        )
                    }
                }
            }
        }
    }
}