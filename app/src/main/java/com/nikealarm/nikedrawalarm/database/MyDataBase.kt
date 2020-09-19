package com.nikealarm.nikedrawalarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SpecialShoesDataModel::class, ShoesDataModel::class], version = 3)
@TypeConverters(Converter::class)
abstract class MyDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        private var instance: MyDataBase? = null
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE SpecialShoesDataModel (SpecialShoesId INTEGER, SpecialShoesSubTitle TEXT, SpecialShoesTitle TEXT, HowToEvent TEXT, SpecialShoesUrl TEXT, SpecialShoesImageUrl TEXT, SpecialShoesMonth TEXT, SpecialShoesDay TEXT, SpecialShoesWhenEvent TEXT) ")
//            }
//        }

        fun getDatabase(context: Context): MyDataBase? {
            if(instance == null) {
                synchronized(MyDataBase::class.java) {
                    instance = Room.databaseBuilder(
                        context,
                        MyDataBase::class.java,
                        "database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return instance
        }
    }
}