package com.nikealarm.nikedrawalarm.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class SpecialShoesDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SpecialShoesId")
    val id: Int?,
    @ColumnInfo(name = "SpecialShoesSubTitle")
    val shoesSubTitle: String,
    @ColumnInfo(name = "SpecialShoesTitle")
    val shoesTitle: String,
    @ColumnInfo(name = "HowToEvent")
    val howToEvent: String? = null,
    @ColumnInfo(name = "SpecialShoesUrl")
    val shoesUrl: String? = null,
    @ColumnInfo(name = "SpecialShoesImageUrl")
    val shoesImage: String? = null,
    @ColumnInfo(name = "SpecialShoesMonth")
    val shoesMonth: String? = null,
    @ColumnInfo(name = "SpecialShoesDay")
    val shoesDay: String? = null,
    @ColumnInfo(name = "SpecialShoesWhenEvent")
    val shoesWhenEvent: String? = null,
    @ColumnInfo(name = "SpecialShoesOrder")
    val shoesOrder: Int? = null
    ) {
    @Ignore
    var isOpened = false

    override fun equals(other: Any?): Boolean {
        other as SpecialShoesDataModel
        return shoesTitle == other.shoesTitle && shoesSubTitle == other.shoesSubTitle
    }
}