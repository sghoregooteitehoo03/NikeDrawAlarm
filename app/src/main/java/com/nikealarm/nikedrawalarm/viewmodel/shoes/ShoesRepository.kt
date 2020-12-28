package com.nikealarm.nikedrawalarm.viewmodel.shoes

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.BuildConfig
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import javax.inject.Inject
import javax.inject.Named

class ShoesRepository @Inject constructor(
    private val mDao: Dao,
    @Named(Contents.PREFERENCE_NAME_UPDATE) private val updatePref: SharedPreferences
) {
    fun getShoesData(shoesCategory: String): LiveData<PagedList<ShoesDataModel>> {
        return LivePagedListBuilder(mDao.getShoesData(shoesCategory), 20).build()
    }

    fun isUpdated() =
        updatePref.getBoolean(BuildConfig.VERSION_CODE.toString(), true)

    fun afterUpdate() {
        with(updatePref.edit()) { // 한번만 보여주게 함
            clear()
            putBoolean(BuildConfig.VERSION_CODE.toString(), false)
            commit()
        }
    }
}