package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.ShoesDataModel

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        MyRepository(application)

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

    // 특정 신발의 웹 주소
    private val url = MutableLiveData<String>()

    fun setUrl(url: String) {
        this.url.value = url
    }

    fun getUrl(): MutableLiveData<String> {
        return url
    }

    val shoesImageUrl = MutableLiveData<String>()

    // Special 목록
    val specialShoesList = repository.getAllSpecialShoesData()
}