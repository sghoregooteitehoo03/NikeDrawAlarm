package com.nikealarm.nikedrawalarm.database

import androidx.room.Ignore

// TODO: 데이터 구조 수정 O
data class SpecialShoesDataModel(
    val ShoesId: Int?,
    val ShoesSubTitle: String,
    val ShoesTitle: String,
    val ShoesPrice: String? = null,
    val ShoesImageUrl: String? = null,
    val ShoesUrl: String? = null,
    val ShoesCategory: String? = null,
    val SpecialTime: Long? = null,
    val SpecialWhenEvent: String? = null,
) {
    @Ignore
    var isOpened = false

    override fun equals(other: Any?): Boolean {
        other as SpecialShoesDataModel
        return (ShoesSubTitle == other.ShoesSubTitle && ShoesTitle == other.ShoesTitle) || ShoesUrl == other.ShoesUrl
    }
}