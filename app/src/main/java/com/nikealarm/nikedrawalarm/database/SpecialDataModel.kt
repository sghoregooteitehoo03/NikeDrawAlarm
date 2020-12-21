package com.nikealarm.nikedrawalarm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class SpecialDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SpecialId")
    val id: Int?,
    @ColumnInfo(name = "SpecialUrl")
    val specialUrl: String,
    @ColumnInfo(name = "SpecialYear")
    val specialYear: String? = null,
    @ColumnInfo(name = "SpecialMonth")
    val specialMonth: String? = null,
    @ColumnInfo(name = "SpecialDay")
    val specialDay: String? = null,
    @ColumnInfo(name = "SpecialWhenEvent")
    val specialWhenEvent: String? = null,
    @ColumnInfo(name = "SpecialOrder")
    val specialOrder: Int? = null
    ) {

    override fun equals(other: Any?): Boolean {
        other as SpecialDataModel
        return specialUrl == other.specialUrl
    }
}