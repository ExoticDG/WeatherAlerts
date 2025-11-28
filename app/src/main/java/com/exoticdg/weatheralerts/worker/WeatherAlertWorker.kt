package com.exoticdg.weatheralerts.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.exoticdg.weatheralerts.network.RetrofitClient
import com.exoticdg.weatheralerts.util.NotificationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WeatherAlertWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val weatherService = RetrofitClient.instance
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    private val sharedPreferences = applicationContext.getSharedPreferences("weather_alerts_prefs", Context.MODE_PRIVATE)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("WeatherAlertWorker", "Starting background work")

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("WeatherAlertWorker", "Location permission not granted. Cannot perform work.")
            return@withContext Result.failure()
        }

        try {
            val location = getCurrentLocation()
            if (location == null) {
                Log.e("WeatherAlertWorker", "Failed to get current location, was null.")
                return@withContext Result.retry()
            }

            val pointString = String.format("%.4f,%.4f", location.latitude, location.longitude)
            Log.d("WeatherAlertWorker", "Fetching alerts for point: $pointString")
            val response = weatherService.getActiveAlertsByPoint(point = pointString)

            if (response.isSuccessful) {
                val alerts = response.body()?.features ?: emptyList()
                val notifiedAlertIds = sharedPreferences.getStringSet("notified_alerts", emptySet()) ?: emptySet()

                val newAlerts = alerts.filter { it.properties?.id != null && !notifiedAlertIds.contains(it.properties.id) }

                if (newAlerts.isNotEmpty()) {
                    Log.d("WeatherAlertWorker", "Found ${newAlerts.size} new alerts.")
                    newAlerts.forEach { alert ->
                        val properties = alert.properties
                        if (properties != null) {
                            NotificationHelper.showNewAlertNotification(
                                context = applicationContext,
                                event = properties.event ?: "Weather Alert",
                                headline = properties.headline ?: "New weather alert for your area."
                            )
                        }
                    }

                    val newNotifiedIds = newAlerts.mapNotNull { it.properties?.id }.toSet()
                    with(sharedPreferences.edit()) {
                        putStringSet("notified_alerts", notifiedAlertIds + newNotifiedIds)
                        apply()
                    }
                } else {
                    Log.d("WeatherAlertWorker", "No new alerts found.")
                }
                return@withContext Result.success()
            } else {
                Log.e("WeatherAlertWorker", "API call failed: ${response.code()} ${response.message()}")
                return@withContext Result.retry()
            }
        } catch (e: IOException) {
            Log.e("WeatherAlertWorker", "Network error during background work", e)
            return@withContext Result.retry()
        } catch (e: Exception) {
            Log.e("WeatherAlertWorker", "Exception during background work", e)
            return@withContext Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationTokenSource = CancellationTokenSource()

        continuation.invokeOnCancellation {
            cancellationTokenSource.cancel()
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location: Location? ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
    }
}