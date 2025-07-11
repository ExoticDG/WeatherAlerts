package com.exoticdg.weatheralerts.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.exoticdg.weatheralerts.R
import com.exoticdg.weatheralerts.databinding.FragmentDashboardBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cancellationTokenSource = CancellationTokenSource()

    // ActivityResultLauncher for location permissions
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    getLocationAndLoadUrl()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    getLocationAndLoadUrl()
                }
                else -> {
                    // No location access granted.
                    Toast.makeText(requireContext(), "Location permission denied. Showing default content.", Toast.LENGTH_LONG).show()
                    dashboardViewModel.loadDefaultUrl() // Load a default URL
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val myWebView: WebView = binding.webviewDashboard1
        myWebView.settings.javaScriptEnabled = true
        // Set a WebViewClient to handle page navigation within the WebView
        myWebView.webViewClient = android.webkit.WebViewClient()


        dashboardViewModel.webViewUrl.observe(viewLifecycleOwner) { url ->
            url?.let {
                myWebView.loadUrl(it)
                Log.d("DashboardFragment", "WebView loading URL: $it")
            }
        }

        // Check and request permissions
        checkLocationPermissions()

        return root
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                getLocationAndLoadUrl()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                // Explain to the user why you need the permission
                // Then request the permission
                Toast.makeText(requireContext(), "Location permission is needed to show relevant weather data.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            else -> {
                // Directly request the permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission") // We check permissions before calling this
    private fun getLocationAndLoadUrl() {
        // Ensure required permissions are actually granted before proceeding
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location permission not granted.", Toast.LENGTH_SHORT).show()
            dashboardViewModel.loadDefaultUrl() // Load default if permission somehow lost
            return
        }

        cancellationTokenSource = CancellationTokenSource() // Reset cancellation token
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("DashboardFragment", "Location success: Lat: ${location.latitude}, Lon: ${location.longitude}")
                    dashboardViewModel.loadUrlWithLocation(location.latitude, location.longitude)
                } else {
                    Log.d("DashboardFragment", "Location was null.")
                    Toast.makeText(requireContext(), "Could not get current location. Showing default.", Toast.LENGTH_SHORT).show()
                    dashboardViewModel.loadDefaultUrl()
                }
            }
            .addOnFailureListener { e ->
                Log.e("DashboardFragment", "Failed to get location", e)
                Toast.makeText(requireContext(), "Failed to get location: ${e.message}", Toast.LENGTH_SHORT).show()
                dashboardViewModel.loadDefaultUrl()
            }
    }

    override fun onStop() {
        super.onStop()
        // Cancel the location request if the fragment is stopped
        cancellationTokenSource.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
