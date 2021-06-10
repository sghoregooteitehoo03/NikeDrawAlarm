package com.nikealarm.nikedrawalarm.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SpecialDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SpecialId")
    val id: Int?,
    @ColumnInfo(name = "SpecialUrl")
    val specialUrl: String,
    @ColumnInfo(name = "SpecialTime")
    val specialTime: Long? = null,
    @ColumnInfo(name = "SpecialWhenEvent")
    val specialWhenEvent: String? = null
) {

    override fun equals(other: Any?): Boolean {
        other as SpecialDataModel
        return specialUrl == other.specialUrl
    }
}