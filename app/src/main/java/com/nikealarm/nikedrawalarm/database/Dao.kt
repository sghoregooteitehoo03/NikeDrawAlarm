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

    @Query("SELECT ShoesId, ShoesSubTitle, ShoesTitle, ShoesPrice, ShoesImageUrl, ShoesUrl, ShoesCategory, SpecialMonth, SpecialDay, SpecialWhenEvent, SpecialOrder FROM ShoesDataModel INNER JOIN SpecialDataModel ON ShoesUrl = SpecialUrl WHERE ShoesCategory = :upcomingCategory ORDER BY SpecialOrder ASC")
    fun getSpecialShoesPagingData(upcomingCategory: String): DataSource.Factory<Int, SpecialShoesDataModel>

    // SpecialData
    @Query("SELECT * FROM SpecialDataModel") // R
    fun getAllSpecialData(): List<SpecialDataModel>

    @Insert(entity = SpecialDataModel::class) // C
    fun insertSpecialData(insertData: SpecialDataModel)

    @Query("UPDATE SpecialDataModel SET SpecialUrl = :specialNewUrl WHERE SpecialUrl = :specialOldUrl") // U
    fun updateSpecialDataUrl(specialNewUrl: String, specialOldUrl: String)

    @Query("DELETE FROM SpecialDataModel") // D
    fun clearSpecialData()

    @Query("DELETE FROM SpecialDataModel WHERE SpecialUrl = :specialUrl") // D
    fun deleteSpecialData(specialUrl: String)

    @Query("SELECT EXISTS(SELECT * FROM SpecialDataModel WHERE SpecialUrl = :specialUrl)")
    fun existsSpecialData(specialUrl: String): Boolean

    // ShoesData
    @Query("SELECT * FROM ShoesDataModel WHERE ShoesCategory = :shoesCategory") // R
    fun getShoesData(shoesCategory: String): DataSource.Factory<Int, ShoesDataModel>

    @Query("SELECT * FROM ShoesDataModel") // R
    fun getAllShoesData(): List<ShoesDataModel>

    @Query("UPDATE ShoesDataModel SET ShoesPrice = :updatePrice, ShoesCategory = :updateCategory WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle") // U
    fun updateShoesCategory(updatePrice: String?, updateCategory: String?, shoesTitle: String, shoesSubTitle: String)

    @Query("UPDATE ShoesDataModel SET ShoesUrl = :updateUrl WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle") // U
    fun updateShoesUrl(updateUrl: String?, shoesTitle: String, shoesSubTitle: String)

    @Insert(entity = ShoesDataModel::class) // C
    fun insertShoesData(insertData: ShoesDataModel)

    @Query("DELETE FROM ShoesDataModel WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle") // D
    fun deleteShoesData(shoesTitle: String, shoesSubTitle: String)

    @Query("DELETE FROM ShoesDataModel") // D
    fun clearShoesData()

    @Query("SELECT * FROM ShoesDataModel WHERE ShoesId = :id") // R
    fun getShoesDataById(id: Int?): ShoesDataModel

    @Query("SELECT EXISTS(SELECT * FROM ShoesDataModel WHERE ShoesTitle = :shoesTitle AND ShoesSubTitle = :shoesSubTitle AND ShoesUrl = :shoesUrl)")
    fun existsShoesData(shoesTitle: String, shoesSubTitle: String, shoesUrl: String): Boolean
}