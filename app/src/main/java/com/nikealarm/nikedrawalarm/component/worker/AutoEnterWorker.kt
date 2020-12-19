package com.nikealarm.nikedrawalarm.component.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.other.JavaScriptInterface
import com.nikealarm.nikedrawalarm.other.WebState
import com.nikealarm.nikedrawalarm.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import javax.inject.Named
import kotlin.random.Random

class AutoEnterWorker @WorkerInject constructor(
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

    private var state: String? = WebState.WEB_LOGIN
    private var isError = false
    private lateinit var shoesData: SpecialShoesDataModel

    override fun startWork(): ListenableFuture<Result> {
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

            with(webView) {
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
                setLayerType(View.LAYER_TYPE_HARDWARE, null)

                loadUrl("https://www.nike.com/kr/launch/login")
                webViewClient = customWebViewClient
                webChromeClient = WebChromeClient()
            }
        }

        return CallbackToFutureAdapter.getFuture { completer ->
            customWebViewClient = object : WebViewClient() {
                var errorMessage = ""
                var shoesUrl: String? = null

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    if (!isStopped && !isError) { // 취소되지 않았을 때
                        if (url == "https://www.nike.com/kr/launch/login?error=true") { // 로그인 실패 시
                            state = WebState.WEB_FAIL
                            errorMessage = WebState.ERROR_LOGIN
                        }

                        when (state) {
                            WebState.WEB_LOGIN -> { // 웹 로그인
                                val nikeId = autoEnterPref.getString(
                                    Contents.AUTO_ENTER_ID,
                                    ""
                                )!!
                                val password = autoEnterPref.getString(
                                    Contents.AUTO_ENTER_PASSWORD,
                                    ""
                                )!!

                                if (nikeId.isNotEmpty() && password.isNotEmpty()) { // 로그인
                                    webView
                                        .loadUrl("javascript:(function(){$('i.brz-icon-checkbox').click(), document.getElementById('j_username').value = '${nikeId}', document.getElementById('j_password').value = '${password}', $('button.button.large.width-max').click()})()")
                                    state = WebState.WEB_AFTER_LOGIN
                                } else { // 오류 처리
                                    errorMessage = WebState.ERROR_OTHER
                                    state = WebState.WEB_FAIL

                                    webView.loadUrl("")
                                }
                            }
                            WebState.WEB_AFTER_LOGIN -> { // 웹 로그인 후
                                shoesUrl = inputData.getString(Contents.WORKER_AUTO_ENTER_INPUT_KEY)

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
                                val size = autoEnterPref.getString(Contents.AUTO_ENTER_SIZE, "")!!

                                if (size.isNotEmpty()) {
                                    javaScriptInterface.setSize(size)
                                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                    CoroutineScope(Dispatchers.Default).launch {
                                        errorMessage = javaScriptInterface.checkData()

                                        withContext(Dispatchers.Main) {
                                            if (errorMessage == WebState.NOT_ERROR) { // 사이즈가 존재하고 응모가 있을 때
                                                webView
                                                    .loadUrl("javascript:(function(){$('#selectSize option[data-value=${size}]').prop('selected', 'selected').change(), $('i.brz-icon-checkbox').click(), $('a#btn-buy.btn-link.xlarge.btn-order.width-max').click()})()")
                                                webView.loadUrl("https://www.nike.com/kr/ko_kr/mypage")

                                                state = WebState.WEB_SUCCESS
                                            } else { // 사이즈가 없거나 응모가 끝났을 때
                                                state = WebState.WEB_FAIL
                                                webView.loadUrl("")
                                            }
                                        }
                                    }
                                } else { // 오류 처리
                                    errorMessage = WebState.ERROR_OTHER
                                    state = WebState.WEB_FAIL

                                    webView.loadUrl("")
                                }
                            }
                            WebState.WEB_SUCCESS -> { // 응모 확인
                                if (url == "https://www.nike.com/kr/ko_kr/mypage") { // My page에서 DrawList로 감
                                    webView.loadUrl("https://www.nike.com/kr/ko_kr/account/theDrawList")
                                } else { // DrawList
                                    webView.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('body')[0].innerHTML);")

                                    CoroutineScope(Dispatchers.Default).launch {
                                        if (javaScriptInterface.isSuccess(shoesUrl)) { // 응모 성공 시
                                            state = null

                                            completer.set(Result.success())
                                            createNotification(true)
                                        } else { // 응모 실패 시
                                            withContext(Dispatchers.Main) {
                                                errorMessage = WebState.ERROR_OTHER
                                                state = WebState.WEB_FAIL

                                                webView.loadUrl("")
                                            }
                                        }
                                    }
                                }
                            }
                            WebState.WEB_FAIL -> { // 오류 처리
                                completer.set(Result.success(workDataOf(Contents.WORKER_AUTO_ENTER_OUTPUT_KEY to errorMessage)))
                                createNotification(false, errorMessage)
                            }
                        }
                    } else if (isError) {
                        createNotification(false, WebState.ERROR_OTHER)
                        completer.set(Result.success())
                    } else {
                        completer.set(Result.success())
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

        val shoesBitmap = Picasso.get()
            .load(shoesData.ShoesImageUrl)
            .get()
        val cancelIntent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        val notification =
            NotificationCompat.Builder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER)
                .setContentTitle("${shoesData.ShoesTitle} - ${shoesData.ShoesSubTitle}")
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
        CoroutineScope(Dispatchers.Default).launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel() // 채널 생성
            }
            val vibrate = LongArray(4).apply {
                set(0, 0)
                set(1, 100)
                set(2, 200)
                set(3, 300)
            }

            if (isError) { // 오류 발생 시
                val notification = NotificationCompat.Builder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER)
                    .setContentTitle("오류")
                    .setContentText(errorMsg)
                    .setVibrate(vibrate)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build()

                with(NotificationManagerCompat.from(applicationContext)) { // 알림 생성
                    notify(
                        System.currentTimeMillis().toInt(),
                        notification
                    )
                }
            } else { // 오류가 아닐 시
                val shoesBitmap = Picasso.get()
                    .load(shoesData.ShoesImageUrl)
                    .get()
                val channelId = (5000..5000 + shoesData.ShoesId!!).random()

                val notification = if (isSuccess) { // 응모 완료 시
                    NotificationCompat.Builder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER)
                        .setContentTitle("응모 완료! ( ${shoesData.ShoesTitle} )")
                        .setContentText("자동응모가 완료되었습니다.")
                        .setVibrate(vibrate)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(shoesBitmap)
                        .build()
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
                    NotificationCompat.Builder(applicationContext, Contents.CHANNEL_ID_AUTO_ENTER)
                        .setContentTitle("응모 실패 ( ${shoesData.ShoesTitle} )")
                        .setContentText(errorMsg)
                        .setVibrate(vibrate)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(shoesBitmap)
                        .addAction(0, "직접 응모하기", goWebsiteIntent)
                        .build()
                }

                with(NotificationManagerCompat.from(applicationContext)) { // 알림 생성
                    notify(
                        channelId,
                        notification
                    )
                }
                deleteShoes(shoesData.ShoesUrl) // 신발 삭제
            }
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
            removeAllCookies(ValueCallback {})
            flush()
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

        mDao.deleteSpecialData(shoesUrl!!)
    }
    // 데이터 베이스 끝
}