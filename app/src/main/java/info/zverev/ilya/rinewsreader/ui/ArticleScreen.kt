package info.zverev.ilya.rinewsreader.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import info.zverev.ilya.rinewsreader.R
import info.zverev.ilya.rinewsreader.io.ArticleStorageClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    title: String,
    url: String,
    navigateBack: () -> Unit,
    articleService: ArticleStorageClient,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                title = { Text(title) },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                articleService.saveArticle(title, url)
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.article_saved),
                                    withDismissAction = true,
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = stringResource(R.string.bookmark),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { p -> ArticleWebView(
        url = url,
        modifier = Modifier.padding(p).fillMaxSize(),
    ) }
}

@Composable
fun ArticleWebView(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = false
                loadUrl(url)
            }
        },
        update = { it.loadUrl(url) },
        modifier = modifier
    )
}