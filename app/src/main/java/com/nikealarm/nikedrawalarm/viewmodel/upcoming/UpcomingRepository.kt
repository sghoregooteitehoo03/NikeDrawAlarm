package com.nikealarm.nikedrawalarm.viewmodel.upcoming

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import javax.inject.Inject

class UpcomingRepository @Inject constructor(private val mDao: Dao) {
    fun getAllSpecialShoesData(): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getAllSpecialShoesPagingData(), 20).build()
    }

    fun getSpecialShoesData(upcomingCategory: String): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getSpecialShoesPagingData(upcomingCategory), 20).build()
    }
}