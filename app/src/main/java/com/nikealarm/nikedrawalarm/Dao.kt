package com.nikealarm.nikedrawalarm

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllShoesData(): List<DrawShoesDataModel>

    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllShoesPagingData(): DataSource.Factory<Int, DrawShoesDataModel>

    @Insert
    suspend fun insertShoesData(insertData: DrawShoesDataModel)

    @Query("DELETE FROM DrawShoesDataModel")
    suspend fun clearShoesData()
}