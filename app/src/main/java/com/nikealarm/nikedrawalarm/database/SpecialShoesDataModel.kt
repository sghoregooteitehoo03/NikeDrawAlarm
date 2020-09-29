package com.nikealarm.nikedrawalarm.database

import androidx.room.Ignore

data class SpecialShoesDataModel(
    val ShoesId: Int?,
    val ShoesSubTitle: String,
    val ShoesTitle: String,
    val ShoesPrice: String? = null,
    val ShoesImageUrl: String? = null,
    val ShoesUrl: String? = null,
    val ShoesCategory: String? = null,
    val SpecialMonth: String? = null,
    val SpecialDay: String? = null,
    val SpecialWhenEvent: String? = null,
    val SpecialOrder: Int? = null
) {
    @Ignore
    var isOpened = false

    override fun equals(other: Any?): Boolean {
        other as SpecialShoesDataModel
        return (ShoesSubTitle == other.ShoesSubTitle && ShoesTitle == other.ShoesTitle) || ShoesUrl == other.ShoesUrl
    }
}