package com.nikealarm.nikedrawalarm.component.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.util.concurrent.ListenableFuture
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.WebState
import kotlinx.android.synthetic.main.fragment_auto_enter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Named

class AutoEnterWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2) private val autoEnterPref: SharedPreferences
) : ListenableWorker(context, workerParams) {
    private val javaScriptInterface = JavaScriptInterface()
    private lateinit var customWebViewClient: WebViewClient
    private lateinit var webView: WebView

    private var state: String? = WebState.WEB_LOGIN

    override fun startWork(): ListenableFuture<Result> {
        CoroutineScope(Dispatchers.Main).launch {
            webView = WebView(applicationContext)

            with(webView) {
                clearCookie()
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.blockNetworkImage = true
                settings.setAppCacheEnabled(true)
                settings.domStorageEnabled = true
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                settings.useWideViewPort = true

                addJavascriptInterface(javaScriptInterface, "Android")
                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                webViewClient = customWebViewClient
                webChromeClient = WebChromeClient()
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                loadUrl("https://www.nike.com/kr/launch/login")
            }
        }

        return CallbackToFutureAdapter.getFuture { completer ->
            customWebViewClient = object : WebViewClient() {
                var errorMessage = ""
                var shoesUrl: String? = null

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    if (url == "https://www.nike.com/kr/launch/login?error=true") { // 로그인 실패 시
                        state = WebState.WEB_FAIL
                        errorMessage = WebState.ERROR_LOGIN
                    }

                    when (state) {
                        WebState.WEB_LOGIN -> { // 웹 로그인
                            val nikeId = autoEnterPref.getString(
                                Contents.AUTO_ENTER_ID,
                                ""
                            )!!
                            val password = autoEnterPref.getString(
                                Contents.AUTO_ENTER_PASSWORD,
                                ""
                            )!!

                            if (nikeId.isNotEmpty() && password.isNotEmpty()) { // 로그인
                                webView
                                    .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${nikeId}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                                state = WebState.WEB_AFTER_LOGIN
                            } else { // 오류 처리
                                errorMessage = WebState.ERROR_OTHER
                                state = WebState.WEB_FAIL

                                webView.loadUrl("")
                            }
                        }
                        WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                            shoesUrl = inputData.getString(Contents.WORKER_AUTO_ENTER_INPUT_KEY)

                            shoesUrl?.let {
                                webView.loadUrl(it)
                                state = WebState.WEB_SELECT_SIZE
                            } ?: let { // 오류처리
                                errorMessage = WebState.ERROR_OTHER
                                state = WebState.WEB_FAIL

                                webView.loadUrl("")
                            }
                        }
                        WebState.WEB_SELECT_SIZE -> { // 신발 사이즈 선택
                            val size = autoEnterPref.getString(Contents.AUTO_ENTER_SIZE, "")!!

                            if (size.isNotEmpty()) {
                                javaScriptInterface.setSize(size)
                                webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                CoroutineScope(Dispatchers.Default).launch {
                                    errorMessage = javaScriptInterface.checkData()

                                    withContext(Dispatchers.Main) {
                                        if (errorMessage == WebState.NOT_ERROR) { // 사이즈가 존재하고 응모가 있을 때
                                            webView
                                                .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                                            webView.loadUrl("https://www.nike.com/kr/ko_kr/mypage")

                                            state = WebState.WEB_SUCCESS
                                        } else { // 사이즈가 없거나 응모가 끝났을 때
                                            state = WebState.WEB_FAIL
                                            webView.loadUrl("")
                                        }
                                    }
                                }
                            } else { // 오류 처리
                                errorMessage = WebState.ERROR_OTHER
                                state = WebState.WEB_FAIL

                                webView.loadUrl("")
                            }
                        }
                        WebState.WEB_SUCCESS -> { // 응모 확인
                            if (url == "https://www.nike.com/kr/ko_kr/mypage") { // My page에서 DrawList로 감
                                webView.loadUrl("https://www.nike.com/kr/ko_kr/account/theDrawList")
                            } else { // DrawList
                                webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                CoroutineScope(Dispatchers.Default).launch {
                                    if (javaScriptInterface.isSuccess(shoesUrl)) { // 응모 성공 시
                                        withContext(Dispatchers.Main) {
                                            state = null

                                            completer.set(Result.success())
                                        }
                                    } else { // 응모 실패 시
                                        withContext(Dispatchers.Main) {
                                            errorMessage = WebState.ERROR_OTHER
                                            state = WebState.WEB_FAIL

                                            webView.loadUrl("")
                                        }
                                    }
                                }
                            }
                        }
                        WebState.WEB_FAIL -> { // 오류 처리
                            completer.set(Result.failure(workDataOf(Contents.WORKER_AUTO_ENTER_OUTPUT_KEY to errorMessage)))
                        }
                    }
                }
            }

            customWebViewClient
        }
    }

    private fun clearCookie() { // 쿠키 삭제
        with(CookieManager.getInstance()) {
            removeAllCookies(ValueCallback {})
            flush()
        }
    }
}