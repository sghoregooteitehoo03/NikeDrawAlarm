package com.nikealarm.nikedrawalarm.other

import android.util.Log
import android.webkit.JavascriptInterface
import org.jsoup.Jsoup

class JavaScriptInterface() {
    private var html: String? = null
    private lateinit var shoesSize: String

    @JavascriptInterface
    fun getHtml(_html: String) {
        Log.i("Check", _html)
        html = _html
    }

    fun checkData(): String {
        while (true) {
            if (html != null) {
                val doc = Jsoup.parse(html)
                val sizeList = doc.select("div.select-box")
                    .select("select")
                    .text()
                    .split(" ")
                val drawState = doc.select("span.btn-buy")
                    .text()

                html = null
                if (drawState != "THE DRAW 응모하기") { // 응모 중인지 확인
                    return WebState.ERROR_END_DRAW
                } else if (!sizeList.contains(shoesSize)) { // 사이즈가 존재하는지 확인
                    return WebState.ERROR_SIZE
                } else {
                    return WebState.NOT_ERROR
                }
            }
        }
    }

    fun isSuccess(shoesUrl: String?): Boolean { // 응모 성공여부 확인
        while(true) {
            if(html != null) {
                val doc = Jsoup.parse(html)
                val shoesList = doc.select("div.order-list")

                shoesList.forEach { element ->
                    val url = "https://www.nike.com" + element.select("span.tit")
                        .select("a")
                        .attr("href")

                    if(url == shoesUrl) {
                        val state = element.select("div.btn-wrap")
                            .select("span")
                            .text()

                        if(state == "응모완료") {
                            return true
                        }
                    }
                }

                return false
            }
        }
    }

    fun setSize(_shoesSize: String) {
        shoesSize = _shoesSize
    }
}