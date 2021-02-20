package com.nikealarm.nikedrawalarm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareDataViewModel : ViewModel() {
    val allowAutoEnter = MutableLiveData<Boolean>()
}