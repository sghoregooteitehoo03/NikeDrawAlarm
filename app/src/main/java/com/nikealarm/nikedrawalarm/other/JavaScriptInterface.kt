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

    fun checkData(): Boolean {
        do {
            if(html != null) {
                val doc = Jsoup.parse(html)
                val sizeList = doc.select("div.select-box")
                    .select("select")
                    .text()
                    .split(" ")
                val drawState = doc.select("div.btn-box")
                    .select("span")
                    .text()

                if (!sizeList.contains(shoesSize) || drawState != "THE DRAW 응모하기") {
                    return false
                }
            }
        } while(html == null)

        return true
    }

    fun setSize(_shoesSize: String) {
        shoesSize = _shoesSize
    }
}