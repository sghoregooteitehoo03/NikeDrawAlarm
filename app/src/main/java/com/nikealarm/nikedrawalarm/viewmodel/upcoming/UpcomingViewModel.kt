package com.nikealarm.nikedrawalarm.viewmodel.upcoming

import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpcomingViewModel @Inject constructor(
    private val repository: UpcomingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val upcomingCategory = MutableLiveData<String>()

    val specialShoesList: LiveData<PagedList<SpecialShoesDataModel>> = Transformations.switchMap(
        upcomingCategory, Function {
            if (it == "DEFAULT") {
                repository.getAllSpecialShoesData()
            } else {
                repository.getSpecialShoesData(it)
            }
        }
    )

    fun setPreference(preferenceKey: String?, timeTrigger: Long) =
        repository.setPreference(preferenceKey, timeTrigger)

    fun removePreference(preferenceKey: String?) =
        repository.removePreference(preferenceKey)

    fun getAllowAlarmPref() =
        repository.allowAlarmPref
}