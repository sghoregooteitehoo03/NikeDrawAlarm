package com.nikealarm.nikedrawalarm.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.WebState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auto_enter.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AutoEnterFragment : Fragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPref: SharedPreferences

    private var state: String? = WebState.WEB_LOGIN
    private lateinit var id: String
    private lateinit var password: String

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

        id = autoEnterPref.getString(Contents.AUTO_ENTER_ID, "")!!
        password = autoEnterPref.getString(Contents.AUTO_ENTER_PASSWORD, "")!!

        with(autoEnterFrag_webView) {
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
                    if(id.isNotEmpty() && password.isNotEmpty()) {
                        autoEnterFrag_webView
                            .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${id}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                        state = WebState.WEB_AFTER_LOGIN
                    }
                }
                WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                    val url = requireActivity().intent.getStringExtra(Contents.DRAW_URL)

                    url?.let {
                        autoEnterFrag_webView
                            .loadUrl(it)
                        state = WebState.WEB_SELECT_SIZE
                    }
                }
                WebState.WEB_SELECT_SIZE -> { // 신발 사이즈 선택
                    autoEnterFrag_webView
                        .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), $('#selectSize option[data-value=250]').prop('selected', 'selected').change(), $('a#btn-login.btn-link.xlarge.btn-order.width-max').click()})()")
                    state = null
                }
                else -> {  }
            }
        }
    }
}