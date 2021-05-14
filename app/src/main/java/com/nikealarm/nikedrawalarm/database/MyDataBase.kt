package com.nikealarm.nikedrawalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SpecialDataModel::class, ShoesDataModel::class], version = 10)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao
}