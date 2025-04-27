// NwsApiService.kt

package com.exoticdg.weatheralerts.network // Adjust package name

import android.util.Log
import com.exoticdg.weatheralerts.data.alerts.NwsAlertsResponse // Import your data class
import retrofit2.Response // Use retrofit2.Response for full response details
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NwsApiService {

    // Base URL: https://api.weather.gov/

    val response: Response<NwsAlertsResponse>

    @GET("alerts/active?") // Endpoint path relative to the base URL
    suspend fun getActiveAlerts(
        // Example: Filter by area (state abbreviation, e.g., "TX", "MN")
        @Query("area") area: String? = null,
        // Example: Filter by zone ID
        @Query("zone") zone: String? = null,
        // Example: Filter by status ('actual' is typical)
        @Query("status") status: String = "actual",
        // Example: Filter by message type ('alert', 'update')
        @Query("message_type") messageType: String = "alert",
        // NWS API requires a User-Agent, often email or website
        @Header("User-Agent") userAgent: String = "(WeatherAlerts, ExoticDarknessGaming)"
    ): Response<NwsAlertsResponse>// Use Response<> to get status codes, headers etc.
    // Use 'suspend' for Coroutines

    @GET("alerts/active?")
    suspend fun getActiveAlertsByPoint(
        // NWS API expects point as "latitude,longitude" string
        @Query("point") point: String, // e.g., "34.05,-118.24"
        @Header("User-Agent") userAgent: String = "(WeatherAlerts, ExoticDarknessGaming)" // **Remember to set this**
    ): Response<NwsAlertsResponse> // Response allows checking status code etc.



}


