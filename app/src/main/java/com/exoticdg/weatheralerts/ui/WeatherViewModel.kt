// WeatherViewModel.kt
package com.exoticdg.weatheralerts.ui

import android.app.Application // Import Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Change to AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
// import androidx.lifecycle.ViewModel // No longer needed
import androidx.lifecycle.viewModelScope
import com.exoticdg.weatheralerts.data.alerts.AlertFeature
import com.exoticdg.weatheralerts.network.RetrofitClient
import com.exoticdg.weatheralerts.util.NotificationHelper // We'll create this helper
import kotlinx.coroutines.launch
import java.io.IOException

// Change ViewModel to AndroidViewModel to get Application context for NotificationHelper
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    val _alerts = MutableLiveData<List<AlertFeature>?>()
    val alerts: LiveData<List<AlertFeature>?> = _alerts

    val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Keep track of the IDs of alerts for which notifications have been sent
    // This is a simple in-memory cache. For persistence, you'd use SharedPreferences or a database.
    private val notifiedAlertIds = mutableSetOf<String>()

    private val notificationHelper = NotificationHelper(application)

    init {
        // Ensure notification channel is created when ViewModel is initialized
        notificationHelper.createNotificationChannel()
    }


    // Function to fetch alerts (your existing function, slightly modified)
    fun fetchAlertsForLocation(lat: Double, lon: Double) {
        _isLoading.value = true
        _errorMessage.value = null

        val pointString = String.format("%.4f,%.4f", lat, lon)
        Log.d("WeatherViewModel", "Fetching alerts for point: $pointString")

        viewModelScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val response = apiService.getActiveAlertsByPoint(point = pointString)

                if (response.isSuccessful) {
                    val nwsResponse = response.body()
                    val newAlerts = nwsResponse?.features

                    _alerts.postValue(newAlerts) // Update LiveData for the UI

                    // --- Notification Logic ---
                    if (!newAlerts.isNullOrEmpty()) {
                        processNewAlertsForNotification(newAlerts)
                    }
                    // --- End Notification Logic ---

                } else {
                    _errorMessage.postValue("Error fetching alerts: ${response.code()} ${response.message()}")
                    _alerts.postValue(null)
                }
            } catch (e: IOException) {
                _errorMessage.postValue("Network error fetching alerts: ${e.message}")
                _alerts.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("An unexpected error occurred: ${e.message}")
                _alerts.postValue(null)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private fun processNewAlertsForNotification(fetchedAlerts: List<AlertFeature>) {
        val trulyNewAlerts = mutableListOf<AlertFeature>()

        for (alert in fetchedAlerts) {
            val alertId = alert.properties?.id // Assuming your AlertProperties has an 'id' field
            // If not, you'll need a unique identifier. 'event' + 'effective' time might work,
            // but a dedicated ID from the API is best if available.
            // For NWS API, the 'id' field in properties (e.g., "https://api.weather.gov/alerts/NWS-IDP-PROD-4066005-3393963")
            // is a good candidate for a unique identifier.

            if (alertId != null && !notifiedAlertIds.contains(alertId)) {
                trulyNewAlerts.add(alert)
                notifiedAlertIds.add(alertId) // Add to our set of notified alerts
                Log.d("WeatherViewModel", "New alert for notification: ${alert.properties?.event} (ID: $alertId)")
            } else if (alertId == null) {
                Log.w("WeatherViewModel", "Alert found without a unique ID: ${alert.properties?.event}")
                // Decide how to handle alerts without a clear unique ID if necessary
                // For now, we'll only notify for alerts with IDs.
            }
        }

        if (trulyNewAlerts.isNotEmpty()) {
            // Send notifications for these new alerts
            // We'll send one summary notification or individual ones.
            // For simplicity, let's send one for the first new alert.
            val firstNewAlert = trulyNewAlerts.first().properties
            if (firstNewAlert != null) {
                notificationHelper.sendWeatherAlertNotification(
                    title = firstNewAlert.event ?: "New Weather Alert",
                    message = firstNewAlert.headline ?: "Check the app for details.",
                    alertId = firstNewAlert.id ?: "unknown_alert_${System.currentTimeMillis()}" // Pass a unique ID for the notification itself
                )
            }
        }
    }
}
