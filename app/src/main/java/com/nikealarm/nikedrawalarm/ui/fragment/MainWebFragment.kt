package com.nikealarm.nikedrawalarm.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel

class MainWebFragment : Fragment() {
    private lateinit var mainWebView: WebView
    private lateinit var mainProgress: ProgressBar
    private lateinit var mainWebRefresh: SwipeRefreshLayout

    private lateinit var mViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_web, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.webFrag_toolbar).apply {
            (activity as MainActivity).setSupportActionBar(this)
            (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        // 인스턴스 설정
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        val url = mViewModel.getUrl().value?:"https://www.nike.com/kr/launch/?type=feed"

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                backPressedCallback.isEnabled = false
                findNavController().navigate(R.id.action_mainWebFragment_to_drawListFragment)
                true
            }
            else -> false
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
                super.setEnabled(false)
                requireActivity().onBackPressed()
            }
        }
    }
}