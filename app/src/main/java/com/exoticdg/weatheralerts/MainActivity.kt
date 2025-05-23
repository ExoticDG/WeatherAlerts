package com.exoticdg.weatheralerts


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.exoticdg.weatheralerts.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val overlayAlertGranted = 0

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.settings,
                R.id.radarFragment,
                R.id.login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        requestLocationPermissions()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            requestNotificationsPermissions()
        }

        requestOverlayPermission()


//        CoroutineScope(Dispatchers.IO).launch {
//            val apiResponse = com.exoticdg.weatheralerts.data.alerts.NWS_API.O_NWS_API.accessJsonLdApi("https://api.weather.gov")
//            withContext(Dispatchers.Main) {
//                apiResponse.forEach { (key, value) ->
//                    Log.d("JsonLdApi", "key: $key")
//                    if (value is Map<*, *>) {
//                        value.forEach { (innerKey, innerValue) ->
//                            Log.d("JsonLdApi", "  $innerKey: $innerValue")
//                        }
//                    }
//                }
//            }
//        }

        setupnotif()

        Log.d("MAIN ACTIVITY", "End of onCreate")

    }
    // end of 'onCreate'


            //Permissions

            fun requestLocationPermissions() {
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

                    //No location permission granteed

                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

            @RequiresApi(33)
            fun requestNotificationsPermissions() {
                val locationPermissionRequest = registerForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    when {
                        permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                            // Notifications granted
                        }

                        else -> {

                            //Notifications not granted


                        }
                    }
                }

                locationPermissionRequest.launch(
                 arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
            )
                )
            }

            private val settingsLauncher = registerForActivityResult(
             ActivityResultContracts.StartActivityForResult()
               ) { result ->
                    // Recheck permission after returning from settings
                    if (canDrawOverlays(this)) {

                        // Permission is now granted

                        val overlayAlertGranted = 1

                    } else {

                        // Permission is still not granted
                        val text = "Please enable overlay permission for full Alert access."
                        val duration = Toast.LENGTH_LONG

                        val toast = Toast.makeText(this, text, duration) // in Activity
                        toast.show()

                        val overlayAlertGranted = 0
                    }
                }


            private fun requestOverlayPermission() {
                if (!canDrawOverlays(this)) {
                    val intent = Intent(
                     Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + packageName)
                    )
                    settingsLauncher.launch(intent)
             } else {
                    //Permission is granted
                    //startOverlayService(context = this)
                }
            }

            // Helper function to check if the app can draw over other apps
            private fun canDrawOverlays(context: Context): Boolean {
                 return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Settings.canDrawOverlays(context)
                 } else {
                        true // In older Android versions, this was granted at install time
                    }
            }

            //End of Permissions

//    fun startOverlayService(context: Context) {
//        val intent = Intent(context, OverlayService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            ContextCompat.startForegroundService(context, intent)
//        } else {
//            context.startService(intent)
//        }
//    }

}



fun setupnotif() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel.
        val name = "CHANNEL_NAME"
        val descriptionText = "CHANNEL_DESCRIPTION"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val CHANNEL_ID = null
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system. You can't change the importance
        // or other notification behaviors after this.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }


    // The id of the group.
    val groupId = "my_group_01"
    // The user-visible name of the group.
    val groupName = "NAME"
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupId, groupName))



}