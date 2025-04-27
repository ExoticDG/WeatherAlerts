// WeatherFragment.kt
package com.exoticdg.weatheralerts.ui // Adjust package name

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels // Use this for fragment-scoped ViewModel
import androidx.lifecycle.Observer
import com.exoticdg.weatheralerts.R // Your R file
import com.exoticdg.weatheralerts.data.alerts.AlertFeature // Import your data class
// ViewModel should ideally be in its own file, but ensure it's accessible
// import com.yourapp.ui.WeatherViewModel
// WeatherFragment.kt (Assuming permission checks are done elsewhere)

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager // Still needed for SuppressLint check
import android.util.Log
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.ProgressBar
//import android.widget.TextView
//import android.widget.Toast
import androidx.core.content.ContextCompat // Still needed for SuppressLint check
import androidx.lifecycle.viewModelScope
import com.exoticdg.weatheralerts.network.RetrofitClient
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
// Required imports for location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import java.io.IOException

//import com.yourapp.R
//import com.yourapp.data.AlertFeature
// ViewModel needs the fetchAlertsForLocation function from the previous example
// import com.yourapp.ui.WeatherViewModel


class AlertFragment : Fragment(R.layout.fragment_alert) {

    private val weatherViewModel: WeatherViewModel by viewModels()

    // Location Client and Cancellation Token
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cancellationTokenSource = CancellationTokenSource()

    // Views
    private lateinit var alertsTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var fetchByLocationButton: Button // Or however you trigger it

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize Views
        alertsTextView = view.findViewById(R.id.alertsTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        fetchByLocationButton = view.findViewById(R.id.fetchByLocationButton) // Example trigger

        // 2. Setup Trigger (e.g., Button Click)
        // Make sure this listener is only set up once
        fetchByLocationButton.setOnClickListener {
            // IMPORTANT: Call your existing permission check logic here first!
            // if (yourPermissionCheckFunctionReturnsTrue()) {
            //    getLocationAndFetchWeather()
            // } else {
            //    // Handle the case where permission is somehow not granted
            //    showError("Location permission is required.")
            // }

            // --- OR --- If you are CERTAIN permission is granted when this is clicked:
            getLocationAndFetchWeather() // Directly call fetch if permission guaranteed
        }

        // Optional: Fetch automatically if permission granted on start?
        // if (yourPermissionCheckFunctionReturnsTrue()) {
        //    getLocationAndFetchWeather()
        // }

        setupObservers()
    }

    /**
     * Fetches the current location and triggers the ViewModel to get weather alerts.
     * IMPORTANT: This function ASSUMES location permissions (FINE or COARSE)
     * have already been granted by the user. Call this *after* your permission checks.
     */
    // Suppress warning because we explicitly check/require permission before calling this
    @SuppressLint("MissingPermission")
    private fun getLocationAndFetchWeather() {
        // Ensure required permissions are actually granted before proceeding
        val hasFineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            showError("Location permission check failed unexpectedly.")
            setLoadingState(false)
            return // Don't proceed without permission
        }

        setLoadingState(true)
        showError(null) // Clear previous errors

        // Use appropriate priority. Balanced is usually good.
        val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
        cancellationTokenSource = CancellationTokenSource() // Reset cancellation token

        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    // 3. Location obtained - Call ViewModel
                    val lat = location.latitude
                    val lon = location.longitude
                    weatherViewModel.fetchAlertsForLocation(lat, lon) // Ensure ViewModel has this function
                } else {
                    showError("Could not get current location. Please ensure location services are enabled.")
                    setLoadingState(false)
                }
            }
            .addOnFailureListener { e ->
                // 4. Handle Failure
                showError("Failed to get location: ${e.message}")
                setLoadingState(false)
            }
    }

    override fun onStop() {
        super.onStop()
        // 5. Cancel request if fragment stops
        cancellationTokenSource.cancel()
    }

    // --- Helper Functions (Keep these from previous example) ---
    private fun setupObservers() {
        // Observer for loading state
        weatherViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) { // Only hide indicator when VM confirms loading finished
                loadingIndicator.visibility = View.GONE
            }
        })
        // Observer for error messages
        weatherViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            showError(errorMessage)
        })
        // Observer for alerts data
        weatherViewModel.alerts.observe(viewLifecycleOwner, Observer { alertFeatures ->
            // ... (logic to display alerts or 'no alerts' message) ...
            // (Same as in the previous detailed example)
            if (alertFeatures != null) {
                alertsTextView.visibility = View.VISIBLE
                displayAlerts(alertFeatures)
                // Hide error message if alerts are successfully displayed
                if (errorTextView.text.startsWith("Error fetching alerts")) {
                    showError(null)
                }
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

    private fun setLoadingState(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        fetchByLocationButton.isEnabled = !isLoading // Disable button while loading
    }

    private fun showError(message: String?) {
        if (message != null) {
            errorTextView.text = message
            errorTextView.visibility = View.VISIBLE
            alertsTextView.visibility = View.GONE
        } else {
            errorTextView.visibility = View.GONE
        }
    }

    private fun displayAlerts(alerts: List<AlertFeature>) {
        // ... (implementation from previous step - unchanged) ...
        if (alerts.isEmpty()) {
            alertsTextView.text = "No active alerts found for your location."
            return
        }
        val formattedText = StringBuilder()
        formattedText.append("Active Alerts for your Location:")
        alerts.forEachIndexed { index, feature ->
            val props = feature.properties
            if (props != null) {
                formattedText.append("--- Alert ${index + 1} ---\n")
                formattedText.append("Type: ${props.event ?: "N/A"}\n")
                formattedText.append("Severity: ${props.severity ?: "N/A"}\n")
                formattedText.append("Areas: ${props.areaDesc ?: "N/A"}\n")
                formattedText.append("Headline: ${props.headline ?: "N/A"}\n")
            }
        }
        alertsTextView.text = formattedText.toString()
        alertsTextView.scrollTo(0,0)

//        alertsTextView.text = " \${AlertProperties.headline} , \${AlertProperties.event} , \${AlertProperties.severity} , \${AlertProperties.areaDesc}"
//        alertsTextView.visibility = View.VISIBLE
//        Log.d("AlertFragment", "\${AlertProperties.headline}")

//        if (alerts.isEmpty()) {
//            alertsTextView.text = "No active alerts found for your location."
//            alertsTextView.visibility = View.VISIBLE
//            Log.d("AlertFragment", "\${props.headline}")
//        } else {
//            alertsTextView.text = " \${props.headline} , \${props.event} , \${props.severity} , \${props.areaDesc}"
//            alertsTextView.visibility = View.VISIBLE
//            Log.d("AlertFragment", "\${props.headline}")
//        }
    }

    }


fun WeatherViewModel.fetchAlertsForLocation(lat: Double, lon: Double) {
    _isLoading.postValue(true) // Use postValue if called from background thread potentially
    _errorMessage.postValue(null)

    // Format the coordinates for the API query parameter
//    val latitude = 36.017739
//    val longitude = -91.126278
    val latitude = lat
    val longitude = lon

    val pointString = String.format("%.4f,%.4f", latitude,longitude) // Format to 4 decimal places
    Log.d("LOCATION", "$latitude, $longitude")

    viewModelScope.launch {
        try {
            val apiService = RetrofitClient.instance
            // Call the new service method
            val response = apiService.getActiveAlertsByPoint(point = pointString)

            if (response.isSuccessful) {
                val nwsResponse = response.body()
                _alerts.postValue(nwsResponse?.features)
            } else {
                // Handle API errors (4xx, 5xx)
                _errorMessage.postValue("Error fetching alerts: ${response.code()} ${response.message()}")
                _alerts.postValue(null) // Clear data on error
            }
        } catch (e: IOException) {
            // Handle network errors
            _errorMessage.postValue("Network error fetching alerts: ${e.message}")
            _alerts.postValue(null)
        } catch (e: Exception) {
            // Handle other errors (like JSON parsing)
            _errorMessage.postValue("An unexpected error occurred: ${e.message}")
            _alerts.postValue(null)
        } finally {
            _isLoading.postValue(false)
        }
    }
}


fun alertnotif() {


}