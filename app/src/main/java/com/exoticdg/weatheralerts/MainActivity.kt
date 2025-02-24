package com.exoticdg.weatheralerts

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.exoticdg.weatheralerts.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.settings, R.id.radarFragment, R.id.login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        @RequiresApi(Build.VERSION_CODES.N)
        fun requestPermissions() {
            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                    }
                    else -> {
                        // Initializing the popup menu and giving the reference as current context
                        val popupMenu = PopupMenu(this@MainActivity, button)

                        // Inflating popup menu from popup_menu.xml file
                        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            // Toast message on menu item clicked
                            Toast.makeText(this@MainActivity, "You Clicked " + menuItem.title, Toast.LENGTH_SHORT).show()
                            true
                        }
                        // Showing the popup menu
                        popupMenu.show()
                    }
                }
            }

            // Before you perform the actual permission request, check whether your app
            // already has the permissions, and whether your app needs to show a permission
            // rationale dialog. For more details, see Request permissions:
            // https://developer.android.com/training/permissions/requesting#request-permission
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    // end of 'onCreate'


}