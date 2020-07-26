package com.nikealarm.nikedrawalarm.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    // Draw 목록
    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllDrawShoesData(): List<DrawShoesDataModel>

    @Query("SELECT * FROM DrawShoesDataModel")
    fun getAllDrawShoesPagingData(): DataSource.Factory<Int, DrawShoesDataModel>

    @Insert(entity = DrawShoesDataModel::class)
    suspend fun insertDrawShoesData(insertData: DrawShoesDataModel)

    @Query("DELETE FROM DrawShoesDataModel")
    suspend fun clearDrawShoesData()

    @Delete(entity = DrawShoesDataModel::class)
    suspend fun deleteDrawShoesData(deleteData: DrawShoesDataModel)

    // 전체 목록
    @Query("SELECT * FROM ShoesDataModel WHERE ShoesCategory = :shoesCategory")
    fun getShoesData(shoesCategory: String): DataSource.Factory<Int, ShoesDataModel>

    @Insert(entity = ShoesDataModel::class)
    suspend fun insertShoesData(insertData: ShoesDataModel)

    @Query("DELETE FROM ShoesDataModel")
    suspend fun clearShoesData()
}