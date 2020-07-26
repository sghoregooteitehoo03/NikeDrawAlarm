package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase
import com.nikealarm.nikedrawalarm.database.ShoesDataModel

class MyRepository(application: Application) {
    private val mDao = MyDataBase.getDatabase(application)!!.getDao()

    // Draw
    fun getAllDrawShoesPagingData(): LiveData<PagedList<DrawShoesDataModel>> {
        return LivePagedListBuilder(mDao.getAllDrawShoesPagingData(), 60).build()
    }

    suspend fun insertDrawShoesData(insertData: DrawShoesDataModel) {
        mDao.insertDrawShoesData(insertData)
    }

    suspend fun clearDrawShoesData() {
        mDao.clearDrawShoesData()
    }

    // 전체 목록
    fun getShoesData(shoesCategory: String): LiveData<PagedList<ShoesDataModel>> {
        return LivePagedListBuilder(mDao.getShoesData(shoesCategory), 60).build()
    }

    suspend fun clearShoesData() {
        mDao.clearShoesData()
    }
}