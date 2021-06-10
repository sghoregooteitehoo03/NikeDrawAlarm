package com.nikealarm.nikedrawalarm.component.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.NotificationBuilder
import com.nikealarm.nikedrawalarm.other.WebState
import com.nikealarm.nikedrawalarm.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import javax.inject.Named
import kotlin.random.Random

// 카카오: https://accounts.kakao.com/login?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fencode_state%3Dfalse%26response_type%3Dcode%26redirect_uri%3Dhttps%253A%252F%252Fwww.nike.com%252Fkr%252Flaunch%252Fsignin%252Fkakao%26client_id%3D19a5f0bf086abcc9b460e13af8a834b6%26state%3Dhttps%3A%2F%2Fwww.nike.com%2Fkr%2Flaunch%2Flogin
@HiltWorker
class AutoEnterWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @Named(Contents.PREFERENCE_NAME_AUTO_ENTER_V2) private val autoEnterPref: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) private val allowAlarmPref: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_TIME) private val timePref: SharedPreferences,
    private val mDao: Dao
) : ListenableWorker(context, workerParams) {
    private val javaScriptInterface = JavaScriptInterface()

    private lateinit var customWebViewClient: WebViewClient
    private lateinit var webView: WebView
    private lateinit var shoesBitmap: Bitmap
    private lateinit var shoesData: SpecialShoesDataModel
    private lateinit var loginWay: String

    private var state: String? = WebState.WEB_LOGIN
    private var errorMessage = ""

    private val timeoutScope = CoroutineScope(Dispatchers.Default)
    private val scope = CoroutineScope(Dispatchers.Default)
    private val loginScope = CoroutineScope(Dispatchers.Default)
    private var isError = false
    private var isLoging = false

    // TODO: 자동응모 취소 시 타임아웃은 그대로 동작 되는 버그
    override fun onStopped() {
        super.onStopped()
        javaScriptInterface.setStopped(true)

        Log.i("AutoCancel", "중단")
    }

    override fun startWork(): ListenableFuture<Result> {
        Log.i("AutoStart", "동작")
        CoroutineScope(Dispatchers.Default).launch {
            val index = mDao.getAllSpecialShoesData()
                .indexOf(
                    SpecialShoesDataModel(
                        null,
                        "",
                        "",
                        null,
                        null,
                        inputData.getString(Contents.WORKER_AUTO_ENTER_INPUT_KEY)
                    )
                )

            if (index != -1) {
                shoesData = mDao.getAllSpecialShoesData()[index]
                setForegroundAsync(createForeground(index))
            } else {
                isError = true
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            webView = WebView(applicationContext)
            loginWay = autoEnterPref.getString(Contents.AUTO_ENTER_LOGIN_WAY, "")!!

            with(webView) {
                clearCookie()
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                settings.setAppCacheEnabled(true)
                settings.domStorageEnabled = true
                settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
                settings.useWideViewPort = true

                addJavascriptInterface(javaScriptInterface, "Android")
                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                when (loginWay) {
                    "나이키" -> loadUrl("https://www.nike.com/kr/launch/login")
                    "카카오톡" -> loadUrl("https://accounts.kakao.com/login?continue=https%3A%2F%2Fkauth.kakao.com%2Foauth%2Fauthorize%3Fencode_state%3Dfalse%26response_type%3Dcode%26redirect_uri%3Dhttps%253A%252F%252Fwww.nike.com%252Fkr%252Flaunch%252Fsignin%252Fkakao%26client_id%3D19a5f0bf086abcc9b460e13af8a834b6%26state%3Dhttps%3A%2F%2Fwww.nike.com%2Fkr%2Flaunch%2Flogin")
                }
                webViewClient = customWebViewClient
                webChromeClient = WebChromeClient()
            }
        }

        return CallbackToFutureAdapter.getFuture { completer ->
            timeoutScope.launch { // 타임아웃 설정
                delay(5 * 60 * 1000)
                stop()

                completer.set(Result.success())
                createNotification(false, WebState.ERROR_OTHER)
            }

            customWebViewClient = object : WebViewClient() {
                var shoesUrl: String? = null

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    if (isLoging) {
                        loginScope.cancel()
                        javaScriptInterface.clearHtml()

                        isLoging = false
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.i("AutoLoading", "로딩")

                    if (!isStopped && !isError) { // 취소되지 않았을 때
                        if (url == "https://www.nike.com/kr/launch/login?error=true") { // 로그인 실패 시
                            Log.i("AutoLoginFail", "로그인 실패")

                            state = WebState.WEB_FAIL
                            errorMessage = WebState.ERROR_LOGIN
                        }

                        when (state) {
                            WebState.WEB_LOGIN -> { // 웹 로그인
                                Log.i("AutoLogin", "로그인")
                                val id = autoEnterPref.getString(
                                    Contents.AUTO_ENTER_ID,
                                    ""
                                )!!
                                val password = autoEnterPref.getString(
                                    Contents.AUTO_ENTER_PASSWORD,
                                    ""
                                )!!

                                if (id.isNotEmpty() && password.isNotEmpty()) { // 로그인
                                    login(id, password)
                                } else { // 오류 처리
                                    errorMessage = WebState.ERROR_OTHER
                                    state = WebState.WEB_FAIL

                                    webView.loadUrl("")
                                }
                            }
                            WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                                Log.i("AutoAfterLogin", "로그인 후")
                                shoesUrl =
                                    inputData.getString(Contents.WORKER_AUTO_ENTER_INPUT_KEY)

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
                                Log.i("AutoSelectSize", "사이즈 선택")
                                val size = autoEnterPref.getString(Contents.AUTO_ENTER_SIZE, "")!!

                                if (size.isNotEmpty()) {
                                    javaScriptInterface.setSize(size)
                                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                    scope.launch {
                                        errorMessage = javaScriptInterface.checkData()

                                        withContext(Dispatchers.Main) {
                                            when (errorMessage) {
                                                WebState.NOT_ERROR -> { // 사이즈가 존재하고 응모가 있을 때
                                                    webView
                                                        .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                                                    webView.loadUrl("https://www.nike.com/kr/ko_kr/mypage")

                                                    state = WebState.WEB_CHECK_DRAWED
                                                }
                                                WebState.STOPPED -> {
                                                    webView.loadUrl("")
                                                }
                                                else -> {
                                                    state = WebState.WEB_FAIL
                                                    webView.loadUrl("")
                                                }
                                            }
                                        }
                                    }
                                } else { // 오류 처리
                                    errorMessage = WebState.ERROR_OTHER
                                    state = WebState.WEB_FAIL

                                    webView.loadUrl("")
                                }
                            }
                            WebState.WEB_CHECK_DRAWED -> { // 응모 확인
                                Log.i("AutoCheckDraw", "응모 여부 확인")
                                if (url == "https://www.nike.com/kr/ko_kr/mypage") { // My page에서 DrawList로 감
                                    webView.loadUrl("https://www.nike.com/kr/ko_kr/account/theDrawList")
                                } else if (url == "https://www.nike.com/kr/ko_kr/account/theDrawList") { // DrawList
                                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                    scope.launch {
                                        val isSuccess = javaScriptInterface.isSuccess(shoesUrl)

                                        withContext(Dispatchers.Main) {
                                            if (isSuccess) { // 응모 성공 시
                                                state = WebState.WEB_SUCCESS

                                                webView.loadUrl("")

                                            } else { // 응모 실패 시
                                                errorMessage = WebState.ERROR_OTHER
                                                state = WebState.WEB_FAIL

                                                webView.loadUrl("")
                                            }
                                        }
                                    }
                                } else {
                                    errorMessage = WebState.ERROR_LOGIN
                                    state = WebState.WEB_FAIL

                                    webView.loadUrl("")
                                }
                            }
                            WebState.WEB_SUCCESS -> { // 성공
                                Log.i("AutoSuccess", "응모 성공")

                                completer.set(Result.success())
                                createNotification(true)
                            }
                            WebState.WEB_FAIL -> { // 오류 처리
                                Log.i("AutoSuccess", "응모 실패: $errorMessage")

                                completer.set(Result.success())
                                createNotification(false, errorMessage)
                            }
                        }
                    } else if (isError) {
                        createNotification(false, WebState.ERROR_OTHER)
                        completer.set(Result.success())
                    } else {
                        completer.setCancelled()
                    }
                }
            }

            customWebViewClient
        }
    }

    private fun createForeground(index: Int): ForegroundInfo { // 포그라운드 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        shoesBitmap = Glide.with(applicationContext)
            .asBitmap()
            .load(shoesData.ShoesImageUrl)
            .submit()
            .get()
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification =
            NotificationCompat.Builder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER)
                .setContentTitle("${shoesData.ShoesSubTitle} - ${shoesData.ShoesTitle}")
                .setContentText("자동응모 진행 중...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(shoesBitmap)
                .setOngoing(true)
                .addAction(0, "취소하기", cancelIntent)
                .build()

        return ForegroundInfo(
            Random(System.currentTimeMillis()).nextInt(200) + index,
            notification
        )
    }

    // 알림 생성
    private fun createNotification(isSuccess: Boolean, errorMsg: String = "") {
        val builder =
            NotificationBuilder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER, "자동응모")
        timeoutScope.cancel()
        scope.cancel()

        if (isError) { // 오류 발생 시
            with(builder) {
                defaultNotification("오류", errorMsg)
                buildNotify(System.currentTimeMillis().toInt())
            }
        } else { // 오류가 아닐 시
            val channelId = (5000..5000 + shoesData.ShoesId!!).random()

            if (isSuccess) { // 응모 완료 시
                with(builder) {
                    imageNotification(
                        "응모 완료! ( ${shoesData.ShoesTitle} )",
                        "자동응모가 완료되었습니다.",
                        shoesBitmap
                    )
                    buildNotify(channelId)
                }
            } else { // 응모 실패 시
                val goWebsiteIntent = PendingIntent.getActivity(
                    applicationContext,
                    channelId,
                    Intent(applicationContext, MainActivity::class.java).also {
                        it.action = Contents.INTENT_ACTION_GOTO_WEBSITE
                        it.putExtra(Contents.DRAW_URL, shoesData.ShoesUrl)
                        it.putExtra(Contents.CHANNEL_ID, channelId)
                    },
                    PendingIntent.FLAG_ONE_SHOT
                )

                with(builder) {
                    imageNotification(
                        "응모 실패 ( ${shoesData.ShoesTitle} )",
                        errorMsg,
                        shoesBitmap
                    )
                    addActions(arrayOf("직접 응모하기"), arrayOf(goWebsiteIntent))
                    buildNotify(channelId)
                }
            }

//            deleteShoes(shoesData.ShoesUrl) // 신발 삭제
        }
    }

    // 알림 채널 생성
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            Contents.CHANNEL_ID_AUTO_ENTER,
            "자동응모",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun clearCookie() { // 쿠키 삭제
        with(CookieManager.getInstance()) {
            removeAllCookies {}
            flush()
        }
    }

    private fun login(id: String, password: String) {
        when (loginWay) {
            "나이키" -> {
                Log.i("AutoLoginNike", "나이키 로그인")
                webView
                    .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${id}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                state = WebState.WEB_AFTER_LOGIN
            }
            "카카오톡" -> {
                // id_email_2, id_password_3, button.btn_g.submit.btn_disabled.btn_type2
                Log.i("AutoLoginKaKao", "카카오 로그인")
                isLoging = true

                webView
                    .loadUrl("javascript:(function(){$('span.ico_account.ico_check').click(), document.getElementById('id_email_2').value = '${id}', document.getElementById('id_password_3').value = '${password}', document.getElementsByClassName('submit')[0].click()})()")
                webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                state = WebState.WEB_AFTER_LOGIN
                loginScope.launch {
                    if (!javaScriptInterface.isLoginKaKao()) {
                        if(isLoging) {
                            withContext(Dispatchers.Main) {
                                errorMessage = WebState.ERROR_LOGIN
                                state = WebState.WEB_FAIL

                                webView.loadUrl("")
                                isLoging = false
                            }
                        }
                    }
                }
            }
        }
    }

    // 데이터 베이스 시작
    private fun deleteShoes(shoesUrl: String?) {
        with(timePref.edit()) {
            remove(shoesUrl)
            commit()
        }
        with(allowAlarmPref.edit()) {
            remove(shoesUrl)
            commit()
        }

        CoroutineScope(Dispatchers.IO).launch {
            mDao.deleteSpecialData(shoesUrl!!)
        }
    }
    // 데이터 베이스 끝
}