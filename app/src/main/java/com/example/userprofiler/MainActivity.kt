package com.mrkimkim.userprofiler

import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.mrkimkim.userprofiler.ui.theme.UserProfilerTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val URL = "https://doctorpresso.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserProfilerTheme {
                WebViewComposable(url = URL)
            }
            // WebView로 웹사이트를 불러오지 못할 때의 Fallback 로직
            // OpenWebPageOnLaunch(URL)
        }
    }
}

// WebView로 웹사이트를 불러오지 못할 때의 Fallback 로직
@Composable
fun OpenWebPageOnLaunch(url: String) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}

@Composable
fun WebViewComposable(url: String) {
    AndroidView(factory = { context ->
        val webView = WebView(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                javaScriptCanOpenWindowsAutomatically = true
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = WebViewClient()
            loadUrl(url)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UserProfilerTheme {
        WebViewComposable(url = MainActivity.URL)
    }
}