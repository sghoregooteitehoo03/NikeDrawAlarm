package com.nikealarm.nikedrawalarm.viewmodel.upcoming

import androidx.arch.core.util.Function
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.SpecialShoesDataModel

class UpcomingViewModel @ViewModelInject constructor(
    private val repository: UpcomingRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
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
}