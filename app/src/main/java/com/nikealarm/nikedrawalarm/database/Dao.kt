package com.nikealarm.nikedrawalarm.database

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
    fun insertDrawShoesData(insertData: DrawShoesDataModel)

    @Query("DELETE FROM DrawShoesDataModel")
    fun clearDrawShoesData()

    @Query("DELETE FROM DrawShoesDataModel WHERE DrawShoesTitle = :shoesTitle AND DrawShoesSubTitle = :shoesSubTitle")
    fun deleteDrawShoesData(shoesTitle: String, shoesSubTitle: String)

    // 전체 목록
    @Query("SELECT * FROM ShoesDataModel WHERE ShoesCategory = :shoesCategory")
    fun getShoesData(shoesCategory: String): DataSource.Factory<Int, ShoesDataModel>

    @Query("SELECT * FROM ShoesDataModel")
    fun getAllShoesData(): List<ShoesDataModel>

    @Query("UPDATE ShoesDataModel SET ShoesPrice = :updatePrice, ShoesCategory = :updateCategory WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle")
    fun updateShoesCategory(updatePrice: String?, updateCategory: String?, shoesTitle: String, shoesSubTitle: String)

    @Query("UPDATE ShoesDataModel SET ShoesUrl = :updateUrl WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle")
    fun updateShoesUrl(updateUrl: String?, shoesTitle: String, shoesSubTitle: String)

    @Insert(entity = ShoesDataModel::class)
    fun insertShoesData(insertData: ShoesDataModel)

    @Delete(entity = ShoesDataModel::class)
    fun deleteShoesData(deleteData: ShoesDataModel)

    @Query("DELETE FROM ShoesDataModel")
    fun clearShoesData()

    @Query("SELECT * FROM ShoesDataModel WHERE ShoesId = :id")
    fun getShoesDataById(id: Int?): ShoesDataModel
}