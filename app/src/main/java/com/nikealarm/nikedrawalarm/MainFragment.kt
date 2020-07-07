package com.nikealarm.nikedrawalarm

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController

class MainFragment : Fragment() {
    private lateinit var mainWebView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = requireActivity().intent.getStringExtra(MainActivity.DRAW_URL)?:"https://www.nike.com/kr/launch/?type=feed"
        // id설정
        mainWebView = view.findViewById<WebView>(R.id.main_webView).apply {
            loadUrl(url)

            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
    }

    private val mWebChromeClient = object : WebChromeClient() {

    }

    // 메뉴 설정
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.mainMenu_setting -> {
                findNavController().navigate(R.id.action_mainFragment_to_settingScreenPreference)
                true
            }
            else -> false
        }
    }
}