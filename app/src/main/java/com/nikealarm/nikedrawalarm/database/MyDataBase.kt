package com.nikealarm.nikedrawalarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DrawShoesDataModel::class, ShoesDataModel::class], version = 1)
@TypeConverters(Converter::class)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        private var instance: MyDataBase? = null

        fun getDatabase(context: Context): MyDataBase? {
            if(instance == null) {
                synchronized(MyDataBase::class.java) {
                    instance = Room.databaseBuilder(
                        context,
                        MyDataBase::class.java,
                        "database")
                        .build()
                }
            }

            return instance
        }
    }
}