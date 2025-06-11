package info.zverev.ilya.rinewsreader.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import info.zverev.ilya.rinewsreader.R
import info.zverev.ilya.rinewsreader.models.ArticleData
import info.zverev.ilya.rinewsreader.io.ArticleFeedViewModel
import kotlinx.coroutines.launch
import nl.jacobras.humanreadable.HumanReadable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    onRoute: (route: Any) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(stringResource(R.string.app_name)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { p ->
        NewsFeed(
            snackbarHostState = snackbarHostState,
            onRoute = onRoute,
            modifier = Modifier.padding(p),
        )
    }
}

@Composable
fun NewsFeed(
    snackbarHostState: SnackbarHostState,
    onRoute: (route: Any) -> Unit,
    viewModel: ArticleFeedViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val columns: Int = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> 2
        Configuration.ORIENTATION_LANDSCAPE -> 3
        else -> 2
    }

    val gridState = rememberLazyGridState()
    val state by viewModel.uiState
    val composableScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(count = columns),
            modifier = Modifier
                .weight(1f),
        ) {
            items(state.articles.size + (if (state.isLoading) 1 else 0)) { index ->
                if (index >= state.articles.size) {
                    ArticlePlaceholder()
                } else {
                    if (index == state.articles.lastIndex) {
                        viewModel.loadArticles(informError = { error ->
                            composableScope.launch {
                                snackbarHostState.showSnackbar(error, withDismissAction = true)
                            }
                        })
                    }
                    ArticleCard(state.articles[index], onClick = {
                        val article = state.articles[index]
                        onRoute(ArticleRoute(article.source, article.articleUrl.toString()))
                    })
                }
            }
        }
        if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
                    .background(Color.Red)
            ) {
                Text(
                    text = state.error ?: "error",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }

}

@Composable
fun ArticlePlaceholder() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}

@Composable
fun ArticleCard(article: ArticleData, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .height(220.dp),
    ) {
        Column {
            if (article.imageUrl != null) {
                AsyncImage(
                    model = article.imageUrl.toString(),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(R.string.article_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Yellow)
                )
            }
            Text(
                text = article.title,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = HumanReadable.timeAgo(article.published),
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.End)
            )
        }
    }
}
