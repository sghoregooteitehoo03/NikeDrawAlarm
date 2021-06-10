package com.nikealarm.nikedrawalarm.other

import android.util.Log
import android.webkit.JavascriptInterface
import org.jsoup.Jsoup

class JavaScriptInterface() {
    private var html: String? = null
    private var isStopped = false
    private lateinit var shoesSize: String

    fun clearHtml() {
        html = null
    }

    @JavascriptInterface
    fun getHtml(_html: String) {
        html = _html
    }

    fun isLoginKaKao(): Boolean {
        while (true) {
            if (!isStopped) {
                Log.i("AutoInfinity", "반복")

                if (html != null) {
                    val doc = Jsoup.parse(html)
                    // TODO: 버그 수정
                    val errorAlert = doc.select("div#errorAlert")
                        .text()

                    Log.i("AutoIsLogin", "로그인 확인 여부: ${errorAlert}")
                    return errorAlert.isEmpty()
                }
            }
        }
    }

    fun checkData(): String {
        while (true) {
            if (!isStopped) {
                Log.i("AutoInfinity", "반복")

                if (html != null) {
                    Log.i("AutoCheckSize", "신발 사이즈 체크")
                    val doc = Jsoup.parse(html)
                    val sizeList = doc.select("div.select-box")
                        .select("select")
                        .text()
                        .split(" ")
                    val drawState = doc.select("span.btn-buy")
                        .text()

                    clearHtml()
                    if (drawState != "THE DRAW 응모하기") { // 응모 중인지 확인
                        return WebState.ERROR_END_DRAW
                    } else if (!sizeList.contains(shoesSize)) { // 사이즈가 존재하는지 확인
                        return WebState.ERROR_SIZE
                    } else {
                        return WebState.NOT_ERROR
                    }
                }
            } else {
                return WebState.STOPPED
            }
        }
    }

    fun isSuccess(shoesUrl: String?): Boolean { // 응모 성공여부 확인
        while (true) {
            if (!isStopped) {
                Log.i("AutoInfinity", "반복")

                if (html != null) {
                    Log.i("AutoCheckSuccessDraw", "응모 성공여부 확인")
                    val doc = Jsoup.parse(html)
                    val shoesList = doc.select("div.order-list")

                    shoesList.forEach { element ->
                        val url = "https://www.nike.com" + element.select("span.tit")
                            .select("a")
                            .attr("href")

                        if (url == shoesUrl) {
                            val state = element.select("div.btn-wrap")
                                .select("span")
                                .text()

                            if (state == "응모완료") {
                                return true
                            }
                        }
                    }

                    return false
                }
            } else {
                return false
            }
        }
    }

    fun setSize(_shoesSize: String) {
        shoesSize = _shoesSize
    }

    fun setStopped(_isStopped: Boolean) {
        isStopped = _isStopped
    }
}