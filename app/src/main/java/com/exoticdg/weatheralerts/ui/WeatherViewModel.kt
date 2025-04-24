// WeatherViewModel.kt
package com.exoticdg.weatheralerts.ui // Adjust package name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exoticdg.weatheralerts.data.alerts.AlertFeature // Import necessary data classes
import com.exoticdg.weatheralerts.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException // Import for exception handling

class WeatherViewModel : ViewModel() {

    // LiveData to hold the list of alerts for the UI
    private val _alerts = MutableLiveData<List<AlertFeature>?>()
    val alerts: LiveData<List<AlertFeature>?> = _alerts // Expose immutable LiveData

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Function to fetch alerts for a specific area (e.g., state)
    fun fetchAlertsForArea(stateCode: String) {
        _isLoading.value = true // Signal loading start
        _errorMessage.value = null // Clear previous errors

        // Launch a coroutine in the ViewModel's scope
        viewModelScope.launch {
            try {
                // Get the API service instance
                val apiService = RetrofitClient.instance
                // Make the network call (suspending function)
                val response = apiService.getActiveAlerts(area = stateCode) // Pass the state code

                if (response.isSuccessful) {
                    // Request successful, process the body
                    val nwsResponse = response.body()
                    _alerts.postValue(nwsResponse?.features) // Update LiveData with the list of features (alerts)
                } else {
                    // Request failed (e.g., 404 Not Found, 500 Server Error)
                    _errorMessage.postValue("Error fetching alerts: ${response.code()} ${response.message()}")
                    _alerts.postValue(null) // Clear alerts on error
                }
            } catch (e: IOException) {
                // Network error (no connection, timeout)
                _errorMessage.postValue("Network error fetching alerts: ${e.message}")
                _alerts.postValue(null)
            } catch (e: Exception) {
                // Other unexpected errors (e.g., JSON parsing issues)
                _errorMessage.postValue("An unexpected error occurred: ${e.message}")
                _alerts.postValue(null)
            } finally {
                _isLoading.postValue(false) // Signal loading finished
            }
        }
    }
}