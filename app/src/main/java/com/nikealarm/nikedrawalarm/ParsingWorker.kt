package com.nikealarm.nikedrawalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import java.util.*

/*
* 자동으로 데이터 갱신하기
* */

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private val mContext = context

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
            val shoesTitle = elementData.select("div.info-sect") // 신발 이름
                .select("div.text-box")
                .select("p.txt-subject")
                .text() + " " + elementData.select("div.info-sect")
                .select("div.text-box")
                .select("p.txt-description")
                .text()

            val shoesInfo = elementData.select("div.info-sect") // 신발 정보
                .select("div.btn-box")
                .select("span")
                .text()

            // draw가 있을 시
            if(shoesInfo == "THE DRAW 진행예정" || channelId == 0) {
                val innerUrl = "https://www.nike.com" + elementData.select("a").attr("href") // 해당 draw 링크창을 읽어옴
                val innerDoc = Jsoup.connect(innerUrl)
                    .userAgent("Mozilla")
                    .get()

                val shoesImageUrl = innerDoc.select("li.uk-width-1-2") // draw 신발 이미지
                    .select("img")
                    .eq(0)
                    .attr("src")
                val shoesBitmap = Picasso.get().load(shoesImageUrl).get()

                /* draw 있을 때 수정하기 */
                val innerElementData = innerDoc.select("span.uk-text-bold")
                val shoesPrice = innerDoc.select("div.price") // draw 신발 가격
                    .text()

                var howToEvent = "" // 이벤트 참여방법
                for(j in 0..2) {
                    howToEvent += innerElementData.select("p")
                        .eq(j)
                        .text() + "\n"
                }

                howToEvent += "\n$shoesPrice"

                // 알림생성
                createNotification(DrawShoesInfo(shoesTitle, howToEvent, shoesBitmap), channelId, innerUrl)
            }

            channelId++
        }
    }

    // 알림 생성
    private fun createNotification(shoesInfo: DrawShoesInfo, channelId: Int, drawUrl: String) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        val mIntent = Intent(mContext, MainActivity::class.java).apply {
            putExtra(MainActivity.CHANNEL_ID, channelId)
            putExtra(MainActivity.DRAW_URL, drawUrl)
        }
        val mPendingIntent = PendingIntent.getActivity(mContext, 100, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(mContext, "Default")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(shoesInfo.shoesTitle)
            .setVibrate(vibrate)
            .setLargeIcon(shoesInfo.shoesImage)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(shoesInfo.shoesImage)
                .bigLargeIcon(null))
            .setContentText(shoesInfo.howToEvent)
            .addAction(0, "응모하러 가기", mPendingIntent)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Default", shoesInfo.shoesTitle, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(mContext)) {
            notify(channelId, notificationBuilder.build())
        }
    }
}