package com.nikealarm.nikedrawalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SpecialDataModel::class, ShoesDataModel::class], version = 7)
@TypeConverters(Converter::class)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao
}