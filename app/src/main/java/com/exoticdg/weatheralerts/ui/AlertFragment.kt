package com.exoticdg.weatheralerts.ui // Adjust package name

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this for fragment-scoped ViewModel
import androidx.lifecycle.Observer
import com.exoticdg.weatheralerts.R // Your R file
import com.exoticdg.weatheralerts.data.alerts.AlertFeature // Import your data class
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager // Still needed for SuppressLint check
import android.util.Log
import androidx.core.content.ContextCompat // Still needed for SuppressLint check
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class AlertFragment : Fragment(R.layout.fragment_alert) {

    private val weatherViewModel: WeatherViewModel by viewModels()

    // Location Client and Cancellation Token
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cancellationTokenSource = CancellationTokenSource()

    // Views
    private lateinit var alertsTextView: TextView
    private lateinit var errorTextView: TextView
    //    private lateinit var loadingIndicator: ProgressBar
    private lateinit var fetchByLocationButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize Views
        alertsTextView = view.findViewById(R.id.alertsTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
//        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        fetchByLocationButton = view.findViewById(R.id.fetchByLocationButton)

        // 2. Setup Trigger (e.g., Button Click)
        fetchByLocationButton.setOnClickListener {
            getLocationAndFetchWeather()
        }

        setupObservers()
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndFetchWeather() {
        val hasFineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            showError("Location permission check failed unexpectedly.")
//            setLoadingState(false)
            return
        }

//        setLoadingState(true)
        showError(null)

        val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    weatherViewModel.fetchAlertsForLocation(location.latitude, location.longitude)
                } else {
                    showError("Could not get current location. Please ensure location services are enabled.")
//                    setLoadingState(false)
                }
            }
            .addOnFailureListener { e ->
                showError("Failed to get location: ${e.message}")
//                setLoadingState(false)
            }
    }

    override fun onStop() {
        super.onStop()
        cancellationTokenSource.cancel()
    }

    private fun setupObservers() {
//        weatherViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
//            setLoadingState(isLoading)
//        })
        weatherViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            showError(errorMessage)
            if (errorMessage != null) {
                alertsTextView.visibility = View.GONE // Hide alerts when error is shown
            }
        })
        weatherViewModel.alerts.observe(viewLifecycleOwner, Observer { alertFeatures ->
            if (alertFeatures != null) {
                showError(null) // Always clear error when new data arrives
                alertsTextView.visibility = View.VISIBLE
                displayAlerts(alertFeatures)
            } else {
                if (weatherViewModel.isLoading.value == false && weatherViewModel.errorMessage.value == null && errorTextView.visibility == View.GONE) {
                    alertsTextView.text = "Click button to fetch alerts for your location."
                    alertsTextView.visibility = View.VISIBLE
                } else {
                    alertsTextView.visibility = View.GONE
                }
            }
        })
    }

//    private fun setLoadingState(isLoading: Boolean) {
//        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
//        fetchByLocationButton.isEnabled = !isLoading
//    }

    private fun showError(message: String?) {
        if (message != null) {
            errorTextView.text = message
            errorTextView.visibility = View.VISIBLE
        } else {
            errorTextView.visibility = View.GONE
        }
    }

    private fun displayAlerts(alerts: List<AlertFeature>) {
        if (alerts.isEmpty()) {
            alertsTextView.text = "No active alerts found for your location."
            return
        }
        val formattedText = StringBuilder()
        formattedText.append("Active Alerts for your Location:")
        alerts.forEachIndexed { index, feature ->
            val props = feature.properties
            if (props != null) {
                formattedText.append("\n\n--- Alert ${index + 1} ---")
                formattedText.append("\n\nEvent: ${props.event ?: "N/A"}")
                formattedText.append("\n\nSeverity: ${props.severity ?: "N/A"}")
                formattedText.append("\n\nAreas: ${props.areaDesc ?: "N/A"}")
                formattedText.append("\n\nHeadline: ${props.headline ?: "N/A"}")
                formattedText.append("\n\nDescription:\n${props.description ?: "N/A"}")
                formattedText.append("\n\nAction:\n${props.instruction ?: "N/A"}")
            }
        }
        alertsTextView.text = formattedText.toString()
        alertsTextView.scrollTo(0,0)
    }
}
