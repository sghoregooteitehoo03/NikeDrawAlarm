package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.fragment.app.Fragment
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.databinding.FragmentTestBinding

class TestFragment : Fragment(R.layout.fragment_test) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTestBinding.bind(view)

        with(binding.myWebview) {
            clearCookie()
            settings.javaScriptEnabled = true
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.setAppCacheEnabled(true)
            settings.domStorageEnabled = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            settings.useWideViewPort = true

            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            webViewClient = mWebClient
            webChromeClient = WebChromeClient()

            loadUrl("https://accounts.kakao.com/login?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fencode_state%3Dfalse%26response_type%3Dcode%26redirect_uri%3Dhttps%253A%252F%252Fwww.nike.com%252Fkr%252Flaunch%252Fsignin%252Fkakao%26client_id%3D19a5f0bf086abcc9b460e13af8a834b6%26state%3Dhttps%3A%2F%2Fwww.nike.com%2Fkr%2Flaunch%2Flogin")
        }
        binding.btn.setOnClickListener {
            val id = "lovehjong@naver.com"
            val password = "ssf1215021019@@@"
            binding.myWebview.loadUrl("javascript:(function(){$('span.ico_account.ico_check').click(), document.getElementById('id_email_2').value = '${id}', document.getElementById('id_password_3').value = '${password}', document.getElementsByClassName('submit')[0].click()})()")
        }
    }

    private val mWebClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.i("Check", "로딩: $url")
        }
    }

    private fun clearCookie() { // 쿠키 삭제
        with(CookieManager.getInstance()) {
            removeAllCookies {}
            flush()
        }
    }
}