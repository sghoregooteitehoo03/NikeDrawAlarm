package com.nikealarm.nikedrawalarm.viewmodel.shoes

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import javax.inject.Inject

class ShoesRepository @Inject constructor(private val mDao: Dao) {
    fun getShoesData(shoesCategory: String): LiveData<PagedList<ShoesDataModel>> {
        return LivePagedListBuilder(mDao.getShoesData(shoesCategory), 20).build()
    }
}