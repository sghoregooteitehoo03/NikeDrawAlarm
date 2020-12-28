package com.nikealarm.nikedrawalarm.viewmodel.shoes

import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import javax.inject.Named

class ShoesViewModel @ViewModelInject constructor(
    private val repository: ShoesRepository,
    @Assisted
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val shoesCategory = MutableLiveData<String>(ShoesDataModel.CATEGORY_RELEASED)

    val shoesList: LiveData<PagedList<ShoesDataModel>> = Transformations.switchMap(shoesCategory) {
        repository.getShoesData(it)
    }

    val isUpdated = MutableLiveData(repository.isUpdated())

    fun afterUpdate() =
        repository.afterUpdate()
}