package com.nikealarm.nikedrawalarm.database

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShoesDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ShoesId")
    val id: Int?,
    @ColumnInfo(name = "ShoesSubTitle")
    val shoesSubTitle: String,
    @ColumnInfo(name = "ShoesTitle")
    val shoesTitle: String,
    @ColumnInfo(name = "ShoesPrice")
    val shoesPrice: String? = null,
    @ColumnInfo(name = "ShoesImageUrl")
    val shoesImageUrl: String? = null,
    @ColumnInfo(name = "ShoesUrl")
    val shoesUrl: String? = null,
    @ColumnInfo(name = "ShoesCategory")
    val shoesCategory: String? = null
) {
    override fun equals(other: Any?): Boolean {
        other as ShoesDataModel
        return shoesSubTitle == other.shoesSubTitle && shoesTitle == other.shoesTitle
    }

    companion object {
        const val CATEGORY_DRAW = "DRAW"
        const val CATEGORY_DRAW_END = "CATEGORY_DRAW_END"
        const val CATEGORY_COMING_SOON = "COMING SOON"
        const val CATEGORY_RELEASED = "RELEASED"
    }
}