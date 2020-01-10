package com.issue.webview

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var startedTime: Long = 0L
    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "onPageFinished took " + getElapsedTime())
                Toast.makeText(applicationContext, "onPageFinished " + getElapsedTime(), Toast.LENGTH_SHORT).show()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                startedTime = System.currentTimeMillis()
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "onPageStarted")
                Toast.makeText(applicationContext, "onPageStarted", Toast.LENGTH_SHORT).show()
            }
        }

// doses not cause the issue, the load time is usually 800ms
//        loadPage()

// cause the issue, check comment there
// on certain devices this causes up to 60 seconds load time!!!
        loadPageWithDelay() //FIXME

    }

    private fun loadPageWithDelay() {
        Log.d(TAG, "loadPageWithDelay")

        handler.postDelayed({
            loadPage()
        }, DELAY) // set to 3000 by default

        // min delay to cause the issue:
        // - on my Samsung A5 android 8.0.0 Chrome WebView 79 is 40ms causes 30 seconds load time
        // - on my Pixel 2 Android 10 Chrome WebView 79 is 30ms causes 12 seconds load time
    }

    private fun loadPage() {
        Log.d(TAG, "loadPage")
        webView.loadUrl("file:///android_res/raw/html_causing_the_issue.html") // raw res file
    }

    private fun getElapsedTime(): String {
        val elapsedTime = System.currentTimeMillis() - startedTime

        return "" + TimeUnit.MILLISECONDS.toSeconds(elapsedTime) + " s or " + elapsedTime + " milliseconds"
    }

    companion object {
        private const val TAG = "WebView"
        private const val DELAY = 3000L
    }
}
