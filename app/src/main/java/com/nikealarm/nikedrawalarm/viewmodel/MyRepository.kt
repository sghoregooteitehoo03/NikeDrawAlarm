package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.MyDataBase

class MyRepository(application: Application) {
    private val mDao = MyDataBase.getDatabase(application)!!.getDao()

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