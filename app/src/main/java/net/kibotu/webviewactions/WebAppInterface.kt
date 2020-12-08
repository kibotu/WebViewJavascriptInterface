package net.kibotu.webviewactions

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.github.florent37.application.provider.ActivityProvider.currentActivity
import com.github.florent37.application.provider.application
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue


class WebAppInterface(webView: WebView) {

    /**
     * Weak reference on webview.
     */
    private val _webView = WeakReference(webView)

    private val webView
        get() = _webView.get()

    /**
     * Returns shared preference instance from current activity.
     */
    private val sharedPreferences
        get() = currentActivity?.getSharedPreferences(WEBVIEW_PREFERENCE_KEY, Context.MODE_PRIVATE)

    /**
     * Invokes back press on current activity.
     */
    @JavascriptInterface
    fun backPress() = currentActivity?.runOnUiThread { currentActivity?.onBackPressed() }

    /**
     * Closes current activity.
     */
    @JavascriptInterface
    fun close() = currentActivity?.finish()

    /**
     * Returns app version name
     */
    @JavascriptInterface
    fun versionName() = BuildConfig.VERSION_NAME

    /**
     * Returns app version code.
     */
    @JavascriptInterface
    fun versionCode() = BuildConfig.VERSION_CODE

    /**
     * Stores value into shared preferences.
     */
    @JavascriptInterface
    fun storePreference(key: String, value: String) = sharedPreferences
        ?.edit()
        ?.apply {
            putString(key, value)
            apply()
        }

    /**
     * Loads value from shared preferences.
     */
    @JavascriptInterface
    fun loadPreference(key: String, default: String = "") = sharedPreferences?.getString(key, "")

    /**
     * Loads value from shared preferences. Showcase for webview to request app with callback.
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    fun loadPreferenceWithCallback(key: String, default: String = "", callbackFunction: String) {
        val value = sharedPreferences?.getString(key, default)
        webView?.post {
            webView?.evaluateJavascript("$callbackFunction('$value');") {
                // callback result if there is any
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    fun postMessageWithCallback(content: String, callback: String? = null) {
        Log.wtf("WebAppInterface", content)
        webView?.post {
            webView?.evaluateJavascript("$callback();") {
                // callback result if there is any
                Log.wtf("WebAppInterface", "done")
            }
        }
    }

    val queue = ConcurrentLinkedQueue<String>()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    fun postMessage(content: String) {
        Log.wtf("WebAppInterface", "content: $content")
        queue.add(content)
        processQueue()
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    fun postMessage(content: String, callback: String? = null) {
        Log.wtf("WebAppInterface", "content: $content")
        queue.add(content)

        processQueue()

        webView?.post {
            webView?.evaluateJavascript("$callback();") {
                // callback result if there is any
                Log.wtf("WebAppInterface", "done")
            }
        }
    }

    private fun processQueue() {
        while(!queue.isEmpty()) {
            val command = queue.poll()
            // runCommand(command)
        }
    }


    companion object {
        /**
         * Shared preference namespace for webview.
         */
        private val WEBVIEW_PREFERENCE_KEY = "${application?.packageName}_webview"
    }
}