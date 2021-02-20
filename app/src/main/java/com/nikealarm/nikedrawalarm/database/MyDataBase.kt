package com.nikealarm.nikedrawalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SpecialDataModel::class, ShoesDataModel::class], version = 8)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao
}