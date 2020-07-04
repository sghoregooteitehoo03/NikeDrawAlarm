package com.nikealarm.nikedrawalarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup

class ParsingWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private lateinit var resultData: Data
    private val mContext = context

    override fun doWork(): Result {
        parsingData()

        return Result.success(resultData)
    }

    // 크롤링
    private fun parsingData() {
        val url = "https://www.nike.com/kr/launch/?type=feed"
        val doc = Jsoup.connect(url) // nike SNKRS창을 읽어옴
            .userAgent("Mozilla")
            .get()

        val elementData = doc.select("a") // 여러개의 신발
        var value = ""

        for(i in 0..5) {
            val shoesTitle = elementData.select("div.info-sect") // 신발 이름
                .select("div.text-box")
                .select("p.txt-subject")
                .eq(i)
                .text() + " " + elementData.select("div.info-sect")
                .select("div.text-box")
                .select("p.txt-description")
                .eq(i)
                .text()

            val shoesPrice = elementData.select("div.info-sect") // 버튼 텍스트
                .select("div.btn-box")
                .select("span")
                .eq(i)
                .text()

            // draw가 있을 시
            if(shoesPrice == "THE DRAW 진행예정") {
                val innerUrl = "https://www.nike.com" + doc.select("input").eq(i).attr("value") // 해당 draw 링크창을 읽어옴
                val innerDoc = Jsoup.connect(innerUrl)
                    .userAgent("Mozilla")
                    .get()

                val imageUrl = innerDoc.select("img") // 신발 이미지
                    .eq(5)
                    .attr("src")
                val innerElementData = innerDoc.select("span.uk-text-bold")
                val realPrice = innerDoc.select("div.price") // 신발 가격
                    .text()

                var eventContent = "" // 이벤트 설명
                for(j in 0..2) {
                    eventContent += innerElementData.select("p")
                        .eq(j)
                        .text() + "\n"
                }

                eventContent += "\n$realPrice"
                createNotification(shoesTitle, eventContent, imageUrl)
            }

            value += "이름: ${shoesTitle}, 가격: ${shoesPrice} \n"
        }

        resultData = Data.Builder()
            .putString("Data", value)
            .build()
    }

    // 알림 생성
    private fun createNotification(shoesName: String, eventContent: String, imageUrl: String) {
        val vibrate = LongArray(4).apply {
            set(0, 0)
            set(1, 100)
            set(2, 200)
            set(3, 300)
        }

        val shoesBitmap = Picasso.get().load(Uri.parse(imageUrl)).get()
        val builder = NotificationCompat.Builder(mContext, "Default")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(shoesName)
            .setVibrate(vibrate)
            .setLargeIcon(shoesBitmap)
            .setStyle(NotificationCompat.BigTextStyle())
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(shoesBitmap)
                .bigLargeIcon(null))
            .setContentText(eventContent)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("Default", shoesName, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        with(NotificationManagerCompat.from(mContext)) {
            notify(1, builder.build())
        }
    }
}