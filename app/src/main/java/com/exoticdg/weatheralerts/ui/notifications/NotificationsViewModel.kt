package com.exoticdg.weatheralerts.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
}


//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.viewModels // Or viewModels() if in Fragment
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.Observer
//import com.exoticdg.weatheralerts.R // Your R file
//import com.exoticdg.weatheralerts.ui.WeatherViewModel
//
//class WeatherActivity : AppCompatActivity() {
//
//    private val weatherViewModel: WeatherViewModel by viewModels()
//
//    private lateinit var alertsTextView: TextView
//    private lateinit var errorTextView: TextView
//    private lateinit var loadingIndicator: ProgressBar
//    private lateinit var stateEditText: EditText
//    private lateinit var fetchButton: Button
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_weather) // Make sure you have this layout
//
//        alertsTextView = findViewById(R.id.alertsTextView)
//        errorTextView = findViewById(R.id.errorTextView)
//        loadingIndicator = findViewById(R.id.loadingIndicator)
//        //stateEditText = findViewById(R.id.stateEditText)
//        fetchButton = findViewById(R.id.fetchButton)
//
//        fetchButton.setOnClickListener {
//            val state = stateEditText.text.toString().trim().uppercase()
//            if (state.isNotEmpty()) {
//                weatherViewModel.fetchAlertsForArea(state)
//            } else {
//                Toast.makeText(this, "Please enter a state code (e.g., TX)", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Observe loading state
//        weatherViewModel.isLoading.observe(this, Observer { isLoading ->
//            loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
//        })
//
//        // Observe error messages
//        weatherViewModel.errorMessage.observe(this, Observer { errorMessage ->
//            if (errorMessage != null) {
//                errorTextView.text = errorMessage
//                errorTextView.visibility = View.VISIBLE
//                alertsTextView.visibility = View.GONE // Hide alert text on error
//            } else {
//                errorTextView.visibility = View.GONE
//            }
//        })
//
//
//        // Observe the alerts LiveData
//        weatherViewModel.alerts.observe(this, Observer { alertFeatures ->
//            if (alertFeatures != null) {
//                alertsTextView.visibility = View.VISIBLE
//                displayAlerts(alertFeatures)
//            } else {
//                // Handle case where alerts are null (could be initial state or error)
//                // Only display "No alerts" if not loading and no error message shown
//                if(weatherViewModel.isLoading.value == false && weatherViewModel.errorMessage.value == null) {
//                    alertsTextView.text = "No active alerts found for this area."
//                    alertsTextView.visibility = View.VISIBLE
//                }
//            }
//        })
//    }
//
//    // Function to format and display alerts
//    private fun displayAlerts(alerts: List<com.exoticdg.weatheralerts.data.alerts.AlertFeature>) {
//        if (alerts.isEmpty()) {
//            alertsTextView.text = "No active alerts found."
//            return
//        }
//
//        val formattedText = StringBuilder()
//        formattedText.append("Active Alerts:\n\n")
//
//        alerts.forEachIndexed { index, feature ->
//            val props = feature.properties
//            if (props != null) {
//                // **Here's where you make it user-friendly!**
//                formattedText.append("--- Alert ${index + 1} ---\n")
//                formattedText.append("Type: ${props.event ?: "N/A"}\n")
//                formattedText.append("Severity: ${props.severity ?: "N/A"}\n")
//                formattedText.append("Areas: ${props.areaDesc ?: "N/A"}\n")
//                formattedText.append("Headline: ${props.headline ?: "N/A"}\n\n")
//                // Optionally add description, effective/expires times (parsing needed for dates)
//                // formattedText.append("Details: ${props.description ?: "N/A"}\n\n")
//            }
//        }
//
//        alertsTextView.text = formattedText.toString()
//    }
//}