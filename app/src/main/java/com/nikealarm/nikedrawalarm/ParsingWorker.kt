package com.nikealarm.nikedrawalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

/*
* 자동으로 데이터 갱신하기
* */

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private val mContext = context
    private val mDao = MyDataBase.getDatabase(mContext)!!.getDao()
    private var isInExist = true

    override fun doWork(): Result {
        parsingData()

        return Result.success()
    }

    // 크롤링
    private fun parsingData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("Mozilla")
            .get()

        val elementsData = doc.select("div.launch-list-item") // 여러개의 신발

        var channelId = 0
        for(elementData in elementsData) {
            val shoesInfo = elementData.select("div.info-sect") // 신발 정보
                .select("div.btn-box")
                .select("span")
                .text()

            // draw가 있을 시
            if(shoesInfo == "THE DRAW 진행예정") {
                val innerUrl = "https://www.nike.com" + elementData.select("a").attr("href") // 해당 draw 링크창을 읽어옴
                val innerDoc = Jsoup.connect(innerUrl)
                    .userAgent("Mozilla")
                    .get()

                // 신발 정보를 가져옴
                val shoesSubTitle = innerDoc.select("h1.txt-subtitle")
                    .text()
                val shoesTitle = innerDoc.select("h5.txt-title")
                    .text()
                val shoesPrice = innerDoc.select("div.price") // draw 신발 가격
                    .text()
                val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // draw 신발 이미지
                    .select("img")
                    .eq(0)
                    .attr("src")
                val shoesBitmap = Picasso.get().load(shoesImageUrl).get()

                /* draw 있을 때 수정하기 */
                val innerElementData = innerDoc.select("span.uk-text-bold")

                var howToEvent = "" // 이벤트 참여방법
                for(j in 0..2) {
                    howToEvent += innerElementData.select("p")
                        .eq(j)
                        .text() + "\n"
                }

                howToEvent += "\n$shoesPrice"

                if(!mDao.getAllShoesData().contains(DrawShoesDataModel(0, shoesSubTitle, shoesTitle))) {
                    // 알림생성
                    createNotification(DrawShoesDataModel(0, shoesSubTitle, shoesTitle, howToEvent, shoesBitmap, innerUrl), channelId)
                    // 데이터베이스에 추가함
                    insertDatabase(DrawShoesDataModel(null, shoesSubTitle, shoesTitle, howToEvent, shoesBitmap, innerUrl))
                } else {
                    isInExist = false
                }
            }

            channelId++
        }

        clearDatabase()
    }

    // 알림 생성
    private fun createNotification(shoesData: DrawShoesDataModel, channelId: Int) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        val learnMoreIntent = Intent(mContext, MainActivity::class.java).apply {
            action = Contents.INTENT_ACTION_GOTO_WEBSITE
            putExtra(Contents.CHANNEL_ID, channelId)
            putExtra(Contents.DRAW_URL, shoesData.url)
        }
        val setAlarmIntent = Intent(mContext, MainActivity::class.java).apply {
            action = Contents.INTENT_ACTION_GOTO_DRAWLIST
        }

        val learnMorePendingIntent = PendingIntent.getActivity(mContext, channelId, learnMoreIntent, PendingIntent.FLAG_ONE_SHOT)
        val setAlarmPendingIntent = PendingIntent.getActivity(mContext, 100, setAlarmIntent, PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder = NotificationCompat.Builder(mContext, "Default")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("${shoesData.shoesSubTitle} - ${shoesData.shoesTitle}")
            .setVibrate(vibrate)
            .setLargeIcon(shoesData.shoesImage)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(shoesData.shoesImage)
                .bigLargeIcon(null))
            .setContentText(shoesData.howToEvent)
            .setAutoCancel(true)
            .addAction(0, "자세히 보기", learnMorePendingIntent)
            .addAction(0, "알림 설정하기", setAlarmPendingIntent)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Default", shoesData.shoesTitle, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(mContext)) {
            notify(channelId, notificationBuilder.build())
        }
    }

    // 데이터베이스 접근
    private fun insertDatabase(insertShoesData: DrawShoesDataModel) {
        CoroutineScope(IO).launch {
            mDao.insertShoesData(insertShoesData)
        }

        isInExist = false
    }

    private fun clearDatabase() {
        if(isInExist && mDao.getAllShoesData().isNotEmpty()) {
            CoroutineScope(IO).launch {
                mDao.clearShoesData()
            }
        }
    }
}