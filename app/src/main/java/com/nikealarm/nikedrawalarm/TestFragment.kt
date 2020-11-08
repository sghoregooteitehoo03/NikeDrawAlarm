package com.nikealarm.nikedrawalarm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.nikealarm.nikedrawalarm.other.WebState
import kotlinx.android.synthetic.main.fragment_test.*

class TestFragment : Fragment() {
    private var state: String? = WebState.WEB_LOGIN
    private val id = "lovehjong@naver.com"
    private val password = "ssf1215021019@@@"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(testFrag_webView) {
            clearCookie()
            settings.javaScriptEnabled = true
            webViewClient = testWebViewClient

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

            when (state) {
                WebState.WEB_LOGIN -> { // 웹 로그인
                    testFrag_webView
                        .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${id}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                    state = WebState.WEB_AFTER_LOGIN
                }
                WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                    testFrag_webView
                        .loadUrl("https://www.nike.com/kr/launch/t/men/fw/basketball/CZ5725-700/veso24/air-jordan-5-retro-se")
                    state = WebState.WEB_SELECT_SIZE
                }
                WebState.WEB_SELECT_SIZE -> { // 신발 사이즈 선택
                    testFrag_webView
                        .loadUrl("javascript:(function(){$('#selectSize option[value=38]').prop('selected', 'selected').change(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max.right').click()})()")
                    state = null
                }
                else -> {  }
            }
        }
    }
}