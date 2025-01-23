package com.exoticdg.weatheralerts.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "ALERT: Weather Conditions Alert @MainActivity/body "
    }
    val text: LiveData<String> = _text
}