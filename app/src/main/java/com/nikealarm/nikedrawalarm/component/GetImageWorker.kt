package com.nikealarm.nikedrawalarm.component

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.nikealarm.nikedrawalarm.other.Contents
import org.jsoup.Jsoup

class GetImageWorker(context: Context, workerParams: WorkerParameters) : Worker(context,
    workerParams
) {
    private val imageMutableList = mutableListOf<String>()

    override fun doWork(): Result {
        val url = inputData.getString(Contents.WORKER_GET_IMAGE_INPUT_KEY)

        if(url != null) {
            parsing(url)

            if(imageMutableList.size != 0) {
                return Result.success(workDataOf(Contents.WORKER_GET_IMAGE_OUTPUT_KEY to imageMutableList.toTypedArray()))
            }
        }

        return Result.failure()
    }

    private fun parsing(url: String) {
        val doc = Jsoup.connect(url)
            .userAgent("19.0.1.84.52")
            .get()

        val elementsData = doc.select("li.uk-width-1-2")
        for(elementData in elementsData) {
            val imageUrl = elementData.select("img")
                .attr("src")

            imageMutableList.add(imageUrl)
            Log.i("Check", "${imageMutableList.size}")
        }
    }
}