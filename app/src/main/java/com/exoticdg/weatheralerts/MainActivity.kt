package com.exoticdg.weatheralerts


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.provider.Settings
import android.provider.Settings.Global.getString
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
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


        setupNotificationChannel(context = this)

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


//fun setupNotificationChannels(context: Context) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
//
//        // Check if notificationManager is not null before proceeding
//            ?: throw IllegalStateException("NotificationManager not found.")
//
//        // Define Channel and Group Constants
//        val channelId = "TORNADO"
//        val channelName = "Tornado Alerts"
//        val channelDescription = "Alerts from you're API choice for tornadoes in the area"
//        val channelImportance = NotificationManager.IMPORTANCE_HIGH
//        val groupId = "Alerts"
//        val groupName = "Alerts"
//
//
//        // Create the notification channel group if it doesn't exist
//        val existingGroup = notificationManager.getNotificationChannelGroup(groupId)
//        if (existingGroup == null) {
//            val channelGroup = NotificationChannelGroup(groupId, groupName)
//            notificationManager.createNotificationChannelGroup(channelGroup)
//        }
//
//        // Create the notification channel if it doesn't exist
//        val existingChannel = notificationManager.getNotificationChannel(channelId)
//        if (existingChannel == null) {
//            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
//                description = channelDescription
//                group = groupId // Assign the channel to the group
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}


//fun setupnotif() {
//
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        // Create the NotificationChannel.
//        val name = "CHANNEL_NAME"
//        val descriptionText = "CHANNEL_DESCRIPTION"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val CHANNEL_ID = null
//        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
//        mChannel.description = descriptionText
//        // Register the channel with the system. You can't change the importance
//        // or other notification behaviors after this.
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(mChannel)
//    }
//
//}


/**
 * Sets up a notification channel for the app.
 *
 * This function ensures that a notification channel is created for the app on
 * Android versions 8.0 (API level 26) and above. Notification channels are a
 * requirement for posting notifications on these Android versions.
 *
 * @param context The application context.
 */
fun setupNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createTornadoNotificationChannel(context)
    }
}

/**
 * Creates the notification channel.
 *
 * This function is called when the Android version is Oreo (API 26) or higher,
 * as notification channels are required on these versions.
 *
 * @param context The application context.
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun createTornadoNotificationChannel(context: Context) {
    // Define the channel ID, name, description, and importance.
    val channelId = "TORNADO_ALERT" // Unique ID for the channel
    val channelName = "Tornado" // User-visible name of the channel
    val channelDescription = "Alerts of tornadic events supplied by your API of choice, by default NWS." // Description of the channel
    val importance = NotificationManager.IMPORTANCE_HIGH // Importance level of the channel

    // Create the notification channel.
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
//        setSound(Uri sound, AudioAttributes audioAttributes)
//        enableVibration(true)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
//
//            setVibrationEffect(VibrationEffect effect)
//
//          } else{
//
//            setVibrationPattern (long[] vibrationPattern)
//
//          }
        //You can setup other parameters here if needed, like light color, sound, etc...
    }

    // Register the channel with the system.
//    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//    notificationManager.createNotificationChannel(channel)
//
//    fun showNotification() {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        val CHANNEL_ID = "TORNADO_ALERT"
//        val notificationBuilder =
//            NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                .setContentTitle("HIGH PRIORITY")
//                .setContentText("Check this dog puppy video NOW!")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
//
//        notificationManager.notify(666, notificationBuilder.build())
//    }
//
//    showNotification()
}