package com.nikealarm.nikedrawalarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MyRepository(application)

    fun getAllShoesPagingData(): LiveData<PagedList<DrawShoesDataModel>> {
        return repository.getAllShoesPagingData()
    }

    fun insertShoesData(insertData: DrawShoesDataModel) = viewModelScope.launch {
        repository.insertShoesData(insertData)
    }

    fun clearShoesData() = viewModelScope.launch {
        repository.clearShoesData()
    }
}