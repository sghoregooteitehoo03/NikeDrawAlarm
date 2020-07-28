package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        MyRepository(application)

    // DRAW
    fun getAllShoesPagingData(): LiveData<PagedList<DrawShoesDataModel>> {
        return repository.getAllDrawShoesPagingData()
    }

    fun insertShoesData(insertData: DrawShoesDataModel) = viewModelScope.launch {
        repository.insertDrawShoesData(insertData)
    }

    fun clearDrawShoesData() = viewModelScope.launch {
        repository.clearDrawShoesData()
    }

    // 전체 목록
    private val shoesCategory = MutableLiveData<String>(ShoesDataModel.CATEGORY_RELEASED)

    fun setShoesCategory(category: String) {
        shoesCategory.value = category
    }

    fun getShoesCategory(): MutableLiveData<String> {
        return shoesCategory
    }

    private val shoesList: LiveData<PagedList<ShoesDataModel>> = Transformations.switchMap(
        shoesCategory, Function {
            repository.getShoesData(it)
        }
    )

    fun getShoesData(): LiveData<PagedList<ShoesDataModel>> {
        return shoesList
    }

    fun clearShoesData() = viewModelScope.launch {
        repository.clearShoesData()
    }

    // 특정 신발의 웹 주소
    private val url = MutableLiveData<String>()

    fun setUrl(url: String) {
        this.url.value = url
    }

    fun getUrl(): MutableLiveData<String> {
        return url
    }
}