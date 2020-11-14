package com.nikealarm.nikedrawalarm.other

import android.util.Log
import android.webkit.JavascriptInterface
import org.jsoup.Jsoup

class JavaScriptInterface() {
    private var html: String? = null
    private lateinit var shoesSize: String

    @JavascriptInterface
    fun getHtml(_html: String) {
        html = _html
    }

    fun checkData(): String {
        do {
            if (html != null) {
                val doc = Jsoup.parse(html)
                val sizeList = doc.select("div.select-box")
                    .select("select")
                    .text()
                    .split(" ")
                val drawState = doc.select("div.btn-box")
                    .select("span")
                    .text()

                if (drawState != "THE DRAW 응모하기") {
                    return WebState.ERROR_END_DRAW
                } else if (!sizeList.contains(shoesSize)) {
                    return WebState.ERROR_SIZE
                }
            }
        } while (html == null)

        return WebState.NOT_ERROR
    }

    fun setSize(_shoesSize: String) {
        shoesSize = _shoesSize
    }
}