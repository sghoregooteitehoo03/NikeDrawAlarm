package com.nikealarm.nikedrawalarm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

class MyRepository(application: Application) {
    private val mDao = MyDataBase.getDatabase(application)!!.getDao()

    fun getAllShoesData(): List<DrawShoesDataModel> {
        return mDao.getAllShoesData()
    }

    fun getAllShoesPagingData(): LiveData<PagedList<DrawShoesDataModel>> {
        return LivePagedListBuilder(mDao.getAllShoesPagingData(), 60).build()
    }

    suspend fun insertShoesData(insertData: DrawShoesDataModel) {
        mDao.insertShoesData(insertData)
    }

    suspend fun clearShoesData() {
        mDao.clearShoesData()
    }
}