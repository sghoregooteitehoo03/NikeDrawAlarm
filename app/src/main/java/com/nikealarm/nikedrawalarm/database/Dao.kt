package com.nikealarm.nikedrawalarm.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    // Draw 목록
    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllShoesData(): List<DrawShoesDataModel>

    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllShoesPagingData(): DataSource.Factory<Int, DrawShoesDataModel>

    @Insert(entity = DrawShoesDataModel::class)
    suspend fun insertShoesData(insertData: DrawShoesDataModel)

    @Query("DELETE FROM DrawShoesDataModel")
    suspend fun clearShoesData()

    @Delete(entity = DrawShoesDataModel::class)
    suspend fun deleteShoesData(deleteData: DrawShoesDataModel)

    @Query("DELETE FROM DrawShoesDataModel WHERE ShoesSubTitle = :subTitle AND ShoesTitle = :title")
    suspend fun deleteShoesData(subTitle: String, title: String)
}