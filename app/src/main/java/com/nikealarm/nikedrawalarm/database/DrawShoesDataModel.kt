package com.nikealarm.nikedrawalarm.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DrawShoesDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "DrawShoesId")
    val id: Int?,
    @ColumnInfo(name = "DrawShoesSubTitle")
    val shoesSubTitle: String,
    @ColumnInfo(name = "DrawShoesTitle")
    val shoesTitle: String,
    @ColumnInfo(name = "HowToEvent")
    val howToEvent: String? = null,
    @ColumnInfo(name = "DrawShoesImage")
    val shoesImage: Bitmap? = null,
    @ColumnInfo(name = "DrawShoesUrl")
    val url: String? = null
    ) {
    override fun equals(other: Any?): Boolean {
        other as DrawShoesDataModel
        return shoesTitle == other.shoesTitle && shoesSubTitle == other.shoesSubTitle
    }
}