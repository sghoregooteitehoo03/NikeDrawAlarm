package com.nikealarm.nikedrawalarm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.nikealarm.nikedrawalarm.database.DrawShoesDataModel
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
        MyRepository(application)

    // DRAW
    fun getAllShoesPagingData(): LiveData<PagedList<DrawShoesDataModel>> {
        return repository.getAllShoesPagingData()
    }

    fun insertShoesData(insertData: DrawShoesDataModel) = viewModelScope.launch {
        repository.insertShoesData(insertData)
    }

    fun clearShoesData() = viewModelScope.launch {
        repository.clearShoesData()
    }

    private val url = MutableLiveData<String>()

    fun setUrl(url: String) {
        this.url.value = url
    }

    fun getUrl(): MutableLiveData<String> {
        return url
    }
}