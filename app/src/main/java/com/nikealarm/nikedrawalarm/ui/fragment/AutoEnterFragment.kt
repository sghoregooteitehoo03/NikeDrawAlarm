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

    private var state: String? = WebState.WEB_LOGIN

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
        with(autoEnterFrag_webView) {
            clearCookie()
            settings.javaScriptEnabled = true
            addJavascriptInterface(javaScriptInterface, "Android")
            webViewClient = testWebViewClient
            webChromeClient = WebChromeClient()

            loadUrl("https://www.nike.com/kr/launch/login")
        }
    }

    private fun clearCookie() {
        with(CookieManager.getInstance()) {
            removeAllCookies(ValueCallback {})
            flush()
        }
    }

    private val testWebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            if (url == "https://www.nike.com/kr/launch/login?error=true") { // 로그인 실패 시
                state = WebState.WEB_FAIL
            }

            when (state) {
                WebState.WEB_LOGIN -> { // 웹 로그인
                    val id = autoEnterPref.getString(Contents.AUTO_ENTER_ID, "")!!
                    val password = autoEnterPref.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

                    if (id.isNotEmpty() && password.isNotEmpty()) {
                        autoEnterFrag_webView
                            .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${id}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                        state = WebState.WEB_AFTER_LOGIN
                    }
                }
                WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                    val shoesUrl = requireActivity().intent.getStringExtra(Contents.DRAW_URL)

                    shoesUrl?.let {
                        autoEnterFrag_webView
                            .loadUrl(it)
                        state = WebState.WEB_SELECT_SIZE
                    }?:let {
                        state = WebState.WEB_FAIL
                        autoEnterFrag_webView.reload()
                    }
                }
                WebState.WEB_SELECT_SIZE -> { // 신발 사이즈 선택
                    val size = autoEnterPref.getString(Contents.AUTO_ENTER_SIZE, "")!!

                    if (size.isNotEmpty()) {
                        javaScriptInterface.setSize(size)
                        autoEnterFrag_webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                        if (javaScriptInterface.checkData()) {
                            autoEnterFrag_webView
                                .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                            state = null
                        } else { // 사이즈가 없거나 응모가 끝났을 때
                            state = WebState.WEB_FAIL
                            autoEnterFrag_webView.reload()
                        }
                    }
                }
                WebState.WEB_FAIL -> { // 오류 처리
                    Toast.makeText(requireContext(), "응모과정중 오류가 발생하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                }
            }
        }
    }
}