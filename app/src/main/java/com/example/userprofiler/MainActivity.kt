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
import androidx.room.Room
import com.mrkimkim.userprofiler.database.RecordDatabase
import com.mrkimkim.userprofiler.database.Record
import com.mrkimkim.userprofiler.ui.theme.UserProfilerTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        }

        GlobalScope.launch {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = format.format(Date())
            val db = RecordDatabase.getInstance(applicationContext)
            db.recordDao().insertAll(Record(0, dateString, System.currentTimeMillis()))
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

/*
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        setContent {
            val count by viewModel.count.collectAsState()
            UserProfilerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    StepBoard("TOTAL STEPS COUNT", count)
                }
            }
        }
    }

// 사용자의 동의를 얻는다
if (ActivityCompat.checkSelfPermission(
        this, Manifest.permission.READ_CALL_LOG
    ) != PackageManager.PERMISSION_GRANTED
) {
    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.READ_CALL_LOG), 101
    );
}

// CallLog.Calls.CONTENT_URI는 통화 기록에 대한 URI입니다
val managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null)

// 각 컬럼의 인덱스를 얻습니다
val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)

// 결과를 출력합니다
while (managedCursor.moveToNext()) {
    val phNumber = managedCursor.getString(number)
    val callType = managedCursor.getString(type)
    val callDate = managedCursor.getString(date)
    val callDayTime = Date(Long.valueOf(callDate))
    val callDuration = managedCursor.getString(duration)
    var dir: String? = null
    val dircode = callType.toInt()
    when (dircode) {
        CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
        CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
        CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
        else -> dir = "ELSE"
    }
    Log.i(
        "CallLog", """
Phone Number: $phNumber
Call Type: $dir
Call Date: $callDayTime
Call duration in sec: $callDuration
""".trimIndent()
    )
}
managedCursor.close()

if (ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACTIVITY_RECOGNITION
    ) != PackageManager.PERMISSION_GRANTED
) {
    // Permission is not granted
    ActivityCompat.requestPermissions(
        this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 100
    )
}

val account = GoogleSignIn.getAccountForExtension(this, fitnessOptions)
Log.d("hyeongyu", "getAccountForExtension")
if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
    Log.d("hyeongyu", "no hasPermissions")
    GoogleSignIn.requestPermissions(
        this, // your activity
        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
        account, fitnessOptions
    )
} else {
    accessGoogleFit()
}
}

private fun accessGoogleFit() {
Fitness.getRecordingClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE).addOnSuccessListener {
        Log.i(TAG, "Subscription was successful!")
    }.addOnFailureListener { e ->
        Log.w(TAG, "There was a problem subscribing ", e)
    }

Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
    .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA).addOnSuccessListener({ result ->
        // Use response data here
        val totalSteps =
            result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
        viewModel.setCount(totalSteps)
        Log.d(TAG, "Total steps: $totalSteps")
    }).addOnFailureListener({ e -> Log.d(TAG, "OnFailure()", e) })
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
super.onActivityResult(requestCode, resultCode, data)
Log.d(TAG, "onActivityResult")
when (resultCode) {
    Activity.RESULT_OK -> when (requestCode) {
        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> accessGoogleFit()
        else -> {
            // Result wasn't from Google Fit
        }
    }

    else -> {
        Log.d(TAG, "Permission not granted" + resultCode.toString());
        // Permission not granted
    }
}
}
*
*/