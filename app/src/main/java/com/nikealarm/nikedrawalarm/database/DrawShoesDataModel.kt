package com.nikealarm.nikedrawalarm.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DrawShoesDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ShoesId")
    val id: Int?,
    @ColumnInfo(name = "ShoesSubTitle")
    val shoesSubTitle: String,
    @ColumnInfo(name = "ShoesTitle")
    val shoesTitle: String,
    @ColumnInfo(name = "HowToEvent")
    val howToEvent: String? = null,
    @ColumnInfo(name = "ShoesImage")
    val shoesImage: Bitmap? = null,
    @ColumnInfo(name = "ShoesUrl")
    val url: String? = null
    ) {
    override fun equals(other: Any?): Boolean {
        other as DrawShoesDataModel
        return shoesTitle == other.shoesTitle
    }
}