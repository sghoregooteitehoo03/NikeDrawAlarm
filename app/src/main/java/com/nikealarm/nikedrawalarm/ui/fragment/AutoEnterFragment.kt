package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.WebState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auto_enter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AutoEnterFragment : Fragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPref: SharedPreferences
    private val javaScriptInterface = JavaScriptInterface()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auto_enter, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 설정
        initView()
    }

    private fun initView() { // 뷰 설정
        with(autoEnterFrag_webView) {
            clearCookie()
            settings.javaScriptEnabled = true
            addJavascriptInterface(javaScriptInterface, "Android")
            webViewClient = testWebViewClient
            webChromeClient = WebChromeClient()

            loadUrl("https://www.nike.com/kr/launch/login")
        }
        autoEnterFrag_exitButton.setOnClickListener {
            terminationApp()
        }
    }

    private fun clearCookie() {
        with(CookieManager.getInstance()) {
            removeAllCookies(ValueCallback {})
            flush()
        }
    }

    private fun terminationApp() {
        activity?.finish()
    }

    private val testWebViewClient = object : WebViewClient() {
        var errorMessage = ""
        var state: String? = WebState.WEB_LOGIN

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (url == "https://www.nike.com/kr/launch/login?error=true") { // 로그인 실패 시
                state = WebState.WEB_FAIL
                errorMessage = WebState.ERROR_LOGIN
            }

            when (state) {
                WebState.WEB_LOGIN -> { // 웹 로그인
                    val id = autoEnterPref.getString(Contents.AUTO_ENTER_ID, "")!!
                    val password = autoEnterPref.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

                    if (id.isNotEmpty() && password.isNotEmpty()) {
                        autoEnterFrag_webView
                            .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${id}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                        state = WebState.WEB_AFTER_LOGIN
                    } else { // 오류 처리
                        errorMessage = WebState.ERROR_OTHER
                        state = WebState.WEB_FAIL

                        autoEnterFrag_webView.loadUrl("")
                    }
                }
                WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                    val shoesUrl: String? = activity?.intent?.getStringExtra(Contents.DRAW_URL)

                    shoesUrl?.let {
                        autoEnterFrag_webView
                            .loadUrl(it)
                        state = WebState.WEB_SELECT_SIZE
                    } ?: let { // 오류 처리
                        errorMessage = WebState.ERROR_OTHER
                        state = WebState.WEB_FAIL

                        autoEnterFrag_webView.loadUrl("")
                    }
                }
                WebState.WEB_SELECT_SIZE -> { // 신발 사이즈 선택
                    val size = autoEnterPref.getString(Contents.AUTO_ENTER_SIZE, "")!!

                    if (size.isNotEmpty()) {
                        javaScriptInterface.setSize(size)
                        autoEnterFrag_webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                        errorMessage = javaScriptInterface.checkData()
                        if (errorMessage == WebState.NOT_ERROR) { // 사이즈가 존재하고 응모가 있을 때
                            autoEnterFrag_webView
                                .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                            state = null

                            success()
                        } else { // 사이즈가 없거나 응모가 끝났을 때
                            state = WebState.WEB_FAIL
                            autoEnterFrag_webView.loadUrl("")
                        }
                    } else { // 오류 처리
                        errorMessage = WebState.ERROR_OTHER
                        state = WebState.WEB_FAIL

                        autoEnterFrag_webView.loadUrl("")
                    }
                }
                WebState.WEB_FAIL -> { // 오류 처리
                    fail(errorMessage)
                }
                else -> {
                }
            }
        }
    }

    private fun success() { // 응모 성공
        animationSuccess()

        activity?.finish()
    }

    private fun fail(errorMessage: String) { // 응모 실패
        autoEnterFrag_errorText.text = errorMessage

        when (errorMessage) {
            WebState.ERROR_LOGIN -> { // 로그인 오류 처리
                animationFailLoginOrSize()
            }
            WebState.ERROR_SIZE -> { // 사이즈 미존재
                animationFailLoginOrSize()
            }
            WebState.ERROR_END_DRAW -> { // Draw 종료
                animationEndDraw()
            }
            WebState.ERROR_OTHER -> { // 기타 오류
                animationFailOther()
            }
        }
    }

    // 애니메이션 설정
    private fun animationSuccess() { // 응모 성공 애니메이션
        autoEnterFrag_progressBar.animate()
            .alpha(0f)
            .setDuration(200)
            .withLayer()
        with(autoEnterFrag_successImage) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .withLayer()
        }

        autoEnterFrag_stateText.text = "응모 완료!"
    }

    private fun animationFailLoginOrSize() { // 로그인 실패 및 사이즈 미 존재 애니메이션
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .withLayer()
        }
    }

    private fun animationEndDraw() { // Draw 종료
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
        autoEnterFrag_goManual_button.visibility = View.GONE
    }

    private fun animationFailOther() { // 기타 오류
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
    }
    // 애니메이션 설정 끝
}