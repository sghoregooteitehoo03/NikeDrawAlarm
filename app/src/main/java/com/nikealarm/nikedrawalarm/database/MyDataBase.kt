package com.nikealarm.nikedrawalarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SpecialDataModel::class, ShoesDataModel::class], version = 6)
@TypeConverters(Converter::class)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao
}