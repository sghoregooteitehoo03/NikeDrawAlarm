package com.nikealarm.nikedrawalarm.component.worker

import android.content.Context
import android.view.View
import android.webkit.*
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoEnterWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        CoroutineScope(Dispatchers.Main).launch {
            with(WebView(applicationContext)) {
                clearCookie()
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.blockNetworkImage = true
                settings.setAppCacheEnabled(true)
                settings.domStorageEnabled = true
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                settings.useWideViewPort = true

//                addJavascriptInterface(javaScriptInterface, "Android")
                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        return Result.success()
                    }
                }
                webChromeClient = WebChromeClient()
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                loadUrl("https://www.nike.com/kr/launch/login")
            }
        }
    }

    private fun clearCookie() { // 쿠키 삭제
        with(CookieManager.getInstance()) {
            removeAllCookies(ValueCallback {})
            flush()
        }
    }
}