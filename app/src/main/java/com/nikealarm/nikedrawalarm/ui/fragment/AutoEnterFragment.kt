package com.nikealarm.nikedrawalarm.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.CustomTabsBuilder
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.WebState
import com.nikealarm.nikedrawalarm.viewmodel.MyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_auto_enter.*
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AutoEnterFragment : Fragment() {
    @Inject
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER)
    lateinit var autoEnterPref: SharedPreferences
    private val javaScriptInterface = JavaScriptInterface()
    private lateinit var mViewModel: MyViewModel
    private lateinit var textJob: Job

    private var state: String? = WebState.WEB_LOGIN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
        return inflater.inflate(R.layout.fragment_auto_enter, container, false)
    }

    // 시작
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인스턴스 설정
        mViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]

        // 뷰 설정
        initView()

        // 옵저버 설정
        mViewModel.retryEnter.observe(viewLifecycleOwner, {
            if (it) {
                retry()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if(textJob.isActive) {
            textJob.cancel()
        }
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            terminationApp()
        }
    }

    private fun initView() { // 뷰 설정
        with(autoEnterFrag_webView) {
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

        textJob = CoroutineScope(Dispatchers.IO).launch {
            val stateText = autoEnterFrag_stateText.text.toString()
            var count = 0

            while(state != null && state != WebState.WEB_FAIL) {
                delay(500)
                Log.i("Check", "응모 중")
                withContext(Dispatchers.Main) {
                    if(count < 3) {
                        autoEnterFrag_stateText.text = autoEnterFrag_stateText.text.toString().plus(".")
                        count++
                    } else {
                        autoEnterFrag_stateText.text = stateText
                        count = 0
                    }
                }
            }
        }

        autoEnterFrag_reloadingButton.setOnClickListener { // 재시도 버튼
            findNavController().navigate(R.id.action_autoEnterFragment_to_reEditDialog)
        }
        autoEnterFrag_goManual_button.setOnClickListener {  // 직접 응모 버튼
            showWeb()
        }
        autoEnterFrag_exitButton.setOnClickListener { // 종료 버튼
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
        findNavController().navigate(R.id.terminationDialog)
    }

    private fun showWeb() {
        val url = requireActivity().intent.getStringExtra(Contents.DRAW_URL)
            ?: "https://www.nike.com/kr/launch/?type=feed"
        val builder = CustomTabsBuilder().getBuilder()

        with(builder) {
            build().launchUrl(requireContext(), Uri.parse(url))
        }
    }

    private val customWebViewClient = object : WebViewClient() {
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
                    shoesUrl = activity?.intent?.getStringExtra(Contents.DRAW_URL)

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

                        CoroutineScope(Dispatchers.IO).launch {
                            errorMessage = javaScriptInterface.checkData()
                            Log.i("CheckErrorMsg", errorMessage)

                            withContext(Dispatchers.Main) {
                                if (errorMessage == WebState.NOT_ERROR) { // 사이즈가 존재하고 응모가 있을 때
                                    autoEnterFrag_webView
                                        .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                                    autoEnterFrag_webView.loadUrl("https://www.nike.com/kr/ko_kr/mypage")

                                    state = WebState.WEB_SUCCESS
                                } else { // 사이즈가 없거나 응모가 끝났을 때
                                    state = WebState.WEB_FAIL
                                    autoEnterFrag_webView.loadUrl("")
                                }
                            }
                        }
                    } else { // 오류 처리
                        errorMessage = WebState.ERROR_OTHER
                        state = WebState.WEB_FAIL

                        autoEnterFrag_webView.loadUrl("")
                    }
                }
                WebState.WEB_SUCCESS -> {
                    if(url == "https://www.nike.com/kr/ko_kr/mypage") { // My page에서 DrawList로 감
                        autoEnterFrag_webView.loadUrl("https://www.nike.com/kr/ko_kr/account/theDrawList")
                    } else { // DrawList
                        autoEnterFrag_webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                        CoroutineScope(Dispatchers.IO).launch {
                            if(javaScriptInterface.isSuccess(shoesUrl)) { // 응모 성공 시
                                withContext(Dispatchers.Main) {
                                    state = null
                                    success()
                                }
                            } else { // 응모 실패 시
                                withContext(Dispatchers.Main) {
                                    errorMessage = WebState.ERROR_OTHER
                                    state = WebState.WEB_FAIL
                                }
                            }
                        }
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
        textJob.cancel()
        animationSuccess()

//        activity?.finish()
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

    private fun retry() { // 재시도
        with(autoEnterFrag_webView) {
            clearCookie()
            loadUrl("https://www.nike.com/kr/launch/login")

            state = WebState.WEB_LOGIN
        }

        animationRetry()
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
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        with(autoEnterFrag_successImage) {
                            setImageResource(R.drawable.ic_check)

                            val animation = drawable as AnimatedVectorDrawable
                            animation.start()
                        }
                    }
                })
                .withLayer()
        }

        autoEnterFrag_stateText.text = "응모 완료!"
    }

    private fun animationFailLoginOrSize() { // 로그인 실패 및 사이즈 미 존재 애니메이션
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }
    }

    private fun animationEndDraw() { // Draw 종료
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
        autoEnterFrag_goManual_button.visibility = View.GONE
    }

    private fun animationFailOther() { // 기타 오류
        autoEnterFrag_loadingLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_loadingLayout.visibility = View.GONE
                }
            })
            .withLayer()
        with(autoEnterFrag_errorLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }

        autoEnterFrag_reloadingButton.visibility = View.GONE
    }

    private fun animationRetry() { // 재시도
        with(autoEnterFrag_loadingLayout) {
            visibility = View.VISIBLE

            animate().alpha(1f)
                .setDuration(200)
                .setListener(null)
                .withLayer()
        }
        autoEnterFrag_errorLayout.animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    autoEnterFrag_errorLayout.visibility = View.GONE
                }
            })
            .withLayer()
    }
    // 애니메이션 설정 끝
}