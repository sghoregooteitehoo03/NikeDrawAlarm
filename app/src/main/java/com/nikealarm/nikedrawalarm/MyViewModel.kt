package com.nikealarm.nikedrawalarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    private val receiveData = MutableLiveData<String>()

    fun setReceiveData(setData: String) {
        receiveData.value = setData
    }

    fun getReceiveData(): MutableLiveData<String> {
        return receiveData
    }
}