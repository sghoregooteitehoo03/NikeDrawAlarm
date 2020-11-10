package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents

class WebFragment : Fragment() {
    private lateinit var mainWebView: WebView
    private lateinit var mainProgress: ProgressBar
    private lateinit var mainWebRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = requireActivity().intent.getStringExtra(Contents.DRAW_URL)
            ?: "https://www.nike.com/kr/launch/?type=feed"

        // id설정
        mainWebView = view.findViewById<WebView>(R.id.main_webView).apply {
            loadUrl(url)

            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = mWebChromeClient
        }
        mainProgress = view.findViewById(R.id.main_webProgress)
        mainWebRefresh = view.findViewById<SwipeRefreshLayout>(R.id.main_webViewRefresh).apply {
            setOnRefreshListener {
                mainWebView.reload()
            }
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            with(mainProgress) {
                visibility = View.VISIBLE
                progress = newProgress

                if (progress == 100) {
                    visibility = View.GONE
                    mainWebRefresh.isRefreshing = false
                }
            }
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (mainWebView.canGoBack()) {
                mainWebView.goBack()
            } else {
                isEnabled = false
                try {
                    requireActivity().onBackPressed()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }
}