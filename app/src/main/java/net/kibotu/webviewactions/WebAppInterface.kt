package net.kibotu.webviewactions

import android.util.Log
import android.webkit.JavascriptInterface

class WebAppInterface {

    private val TAG = WebAppInterface::class.java.name

    @JavascriptInterface
    fun backPress() {
        Log.v(TAG, "backPress")
    }

    @JavascriptInterface
    fun close() {
        Log.v(TAG, "close")
    }
}