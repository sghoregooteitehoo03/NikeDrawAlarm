package com.nikealarm.nikedrawalarm.viewmodel.shoes

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShoesViewModel @Inject constructor(
    private val repository: ShoesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val shoesCategory = MutableLiveData<String>(ShoesDataModel.CATEGORY_RELEASED)

    val shoesList: LiveData<PagedList<ShoesDataModel>> = Transformations.switchMap(shoesCategory) {
        repository.getShoesData(it)
    }

    fun isUpdated() =
        repository.isUpdated()

    fun afterUpdate() =
        repository.afterUpdate()
}