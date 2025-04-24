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

// Pass the layout ID to the Fragment constructor
class WeatherFragment : Fragment(R.layout.fragment_alert) {

    // Use 'viewModels()' delegate for a ViewModel scoped to this Fragment
    private val weatherViewModel: WeatherViewModel by viewModels()

    // Declare view variables - initialize them in onViewCreated
    private lateinit var alertsTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var stateEditText: EditText
    private lateinit var fetchButton: Button

    // onCreateView is simplified when passing layout ID to constructor.
    // You only override it if you need more complex view creation logic
    // or if using View Binding manually.
    /*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment if not using constructor injection
        // return inflater.inflate(R.layout.fragment_weather, container, false)
    }
    */

    // Logic that needs the view hierarchy should go in onViewCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views using the 'view' parameter passed to onViewCreated
        alertsTextView = view.findViewById(R.id.alertsTextView)
        errorTextView = view.findViewById(R.id.errorTextView)
        loadingIndicator = view.findViewById(R.id.loadingIndicator)
       stateEditText = view.findViewById(R.id.stateEditText)
        fetchButton = view.findViewById(R.id.fetchButton)

        // Setup listeners
        fetchButton.setOnClickListener {
            val state = stateEditText.text.toString().trim().uppercase()
            if (state.length == 2) { // Basic validation
                weatherViewModel.fetchAlertsForArea(state)
            } else {
                // Use requireContext() to get context safely in a Fragment
                Toast.makeText(requireContext(), "Please enter a 2-letter state code (e.g., TX)", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup Observers - Use 'viewLifecycleOwner' here!
        setupObservers()
    }

    private fun setupObservers() {
        // Observe loading state
        weatherViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        // Observe error messages
        weatherViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null) {
                errorTextView.text = errorMessage
                errorTextView.visibility = View.VISIBLE
                alertsTextView.visibility = View.GONE // Hide alert text on error
            } else {
                errorTextView.visibility = View.GONE
            }
        })

        // Observe the alerts LiveData
        weatherViewModel.alerts.observe(viewLifecycleOwner, Observer { alertFeatures ->
            if (alertFeatures != null) {
                alertsTextView.visibility = View.VISIBLE
                displayAlerts(alertFeatures)
            } else {
                // Handle null case (initial or error) only if not loading and no error shown
                if (weatherViewModel.isLoading.value == false && weatherViewModel.errorMessage.value == null) {
                    alertsTextView.text = "Enter a state code to fetch alerts." // Initial message
                    alertsTextView.visibility = View.VISIBLE
                } else if (weatherViewModel.isLoading.value == false) {
                    // If not loading but alerts became null (e.g., error occurred), hide it
                    // Error message will be shown by its own observer
                    alertsTextView.visibility = View.GONE
                }
            }
        })
    }

    // This helper function remains largely the same
    private fun displayAlerts(alerts: List<AlertFeature>) {
        if (alerts.isEmpty()) {
            alertsTextView.text = "No active alerts found for this area."
            return
        }

        val formattedText = StringBuilder()
        formattedText.append("Active Alerts:\n\n")

        alerts.forEachIndexed { index, feature ->
            val props = feature.properties
            if (props != null) {
                formattedText.append("--- Alert ${index + 1} ---\n")
                formattedText.append("Type: ${props.event ?: "N/A"}\n")
                formattedText.append("Severity: ${props.severity ?: "N/A"}\n")
                formattedText.append("Areas: ${props.areaDesc ?: "N/A"}\n")
                formattedText.append("Headline: ${props.headline ?: "N/A"}\n\n")
                // You can add more details like description, time, etc.
                // Remember to parse/format dates if needed
            }
        }

        alertsTextView.text = formattedText.toString()
        alertsTextView.scrollTo(0,0) // Scroll to top when new alerts are displayed
    }
}