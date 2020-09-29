package com.nikealarm.nikedrawalarm.database

import androidx.paging.DataSource
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    // SpecialShoesData
    @Query("SELECT ShoesId, ShoesSubTitle, ShoesTitle, ShoesPrice, ShoesImageUrl, ShoesUrl, ShoesCategory, SpecialMonth, SpecialDay, SpecialWhenEvent, SpecialOrder FROM ShoesDataModel INNER JOIN SpecialDataModel ON ShoesUrl = SpecialUrl ORDER BY SpecialOrder ASC")
    fun getAllSpecialShoesData(): List<SpecialShoesDataModel>

    @Query("SELECT ShoesId, ShoesSubTitle, ShoesTitle, ShoesPrice, ShoesImageUrl, ShoesUrl, ShoesCategory, SpecialMonth, SpecialDay, SpecialWhenEvent, SpecialOrder FROM ShoesDataModel INNER JOIN SpecialDataModel ON ShoesUrl = SpecialUrl ORDER BY SpecialOrder ASC")
    fun getAllSpecialShoesPagingData(): DataSource.Factory<Int, SpecialShoesDataModel>

    // SpecialData
    @Query("SELECT * FROM SpecialDataModel")
    fun getAllSpecialData(): List<SpecialDataModel>

    @Insert(entity = SpecialDataModel::class)
    fun insertSpecialData(insertData: SpecialDataModel)

    @Query("UPDATE SpecialDataModel SET SpecialUrl = :specialNewUrl WHERE SpecialUrl = :specialOldUrl")
    fun updateSpecialDataUrl(specialNewUrl: String, specialOldUrl: String)

    @Query("DELETE FROM SpecialDataModel")
    fun clearSpecialData()

    @Query("DELETE FROM SpecialDataModel WHERE SpecialUrl = :specialUrl")
    fun deleteSpecialData(specialUrl: String)

    // ShoesData
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

    @Query("DELETE FROM ShoesDataModel WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle")
    fun deleteShoesData(shoesTitle: String, shoesSubTitle: String)

    @Query("DELETE FROM ShoesDataModel")
    fun clearShoesData()

    @Query("SELECT * FROM ShoesDataModel WHERE ShoesId = :id")
    fun getShoesDataById(id: Int?): ShoesDataModel
}