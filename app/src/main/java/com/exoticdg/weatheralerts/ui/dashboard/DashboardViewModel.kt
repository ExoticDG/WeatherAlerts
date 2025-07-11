package com.exoticdg.weatheralerts.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private val _webViewUrl = MutableLiveData<String>()
    val webViewUrl: LiveData<String> = _webViewUrl

    // Function to update the URL based on latitude and longitude
    fun loadUrlWithLocation(latitude: Double, longitude: Double) {
        _webViewUrl.value = "https://beta.weather.gov/point/$latitude/$longitude"
    }

    // Optional: A way to load a default URL if location is not available
    fun loadDefaultUrl() {
        _webViewUrl.value = "https://beta.weather.gov/" // Or some other default
    }

    // Remove the init block that tries to access latitude/longitude directly
    // init {
    //    loadUrl("https://beta.weather.gov/point/$latitude,$longitude") // Initial URL
    // }
}