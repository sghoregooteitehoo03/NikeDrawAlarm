package com.nikealarm.nikedrawalarm.viewmodel.upcoming

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.Dao
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import javax.inject.Inject
import javax.inject.Named

class UpcomingRepository @Inject constructor(
    private val mDao: Dao,
    @Named(Contents.PREFERENCE_NAME_TIME) private val timePref: SharedPreferences,
    @Named(Contents.PREFERENCE_NAME_ALLOW_ALARM) val allowAlarmPref: SharedPreferences
) {
    fun getAllSpecialShoesData(): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getAllSpecialShoesPagingData(), 20).build()
    }

    fun getSpecialShoesData(upcomingCategory: String): LiveData<PagedList<SpecialShoesDataModel>> {
        return LivePagedListBuilder(mDao.getSpecialShoesPagingData(upcomingCategory), 20).build()
    }

    fun setPreference(preferenceKey: String?, timeTrigger: Long) {
        with(timePref.edit()) {
            putLong(preferenceKey, timeTrigger)
            commit()
        }

        with(allowAlarmPref.edit()) {
            putBoolean(preferenceKey, true)
            commit()
        }
    }

    fun removePreference(preferenceKey: String?) {
        with(timePref.edit()) {
            remove(preferenceKey)
            commit()
        }

        with(allowAlarmPref.edit()) {
            remove(preferenceKey)
            commit()
        }
    }
}