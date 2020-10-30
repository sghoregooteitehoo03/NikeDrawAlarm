package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.*
import javax.inject.Inject

class MyRepository(application: Application) {
    private val mDao = MyDataBase.getDatabase(application)!!.getDao()

    // ShoesData
    fun getShoesData(shoesCategory: String): LiveData<PagedList<ShoesDataModel>> {
        return LivePagedListBuilder(mDao.getShoesData(shoesCategory), 20).build()
    }

    // SpecialShoesData
    fun getAllSpecialShoesData(): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getAllSpecialShoesPagingData(), 20).build()
    }

    fun getSpecialShoesData(upcomingCategory: String): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getSpecialShoesPagingData(upcomingCategory), 20).build()
    }
}