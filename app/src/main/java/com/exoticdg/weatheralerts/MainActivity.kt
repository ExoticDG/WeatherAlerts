package com.exoticdg.weatheralerts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.Menu
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.exoticdg.weatheralerts.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
// import androidx.privacysandbox.tools.core.generator.build
import okhttp3.Interceptor
import com.google.gson.Gson
import okhttp3.Response
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    val anchorView = coordinatorLayout
    val notifacationsnackbar1 = Snackbar.make(anchorView, "Well. This app sent very useful without notification.", LENGTH_SHORT)
    val notifacationsnackbar2 = Snackbar.make(anchorView, "Remember? You made this app useless by disabling notifications.", LENGTH_SHORT)
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {

            } else {
                notifacationsnackbar1.show()
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    val CHANNEL_ID = "Alerts"
    val CHANNEL_NAME = "Alerts"
    val CHANNEL_ID2 = "Tests"
    val CHANNEL_NAME2 = "Tests"
    val NOTIF_ID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_map, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        createNotifChannel()

         val intent= Intent(this,MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
        }
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("headline")
            .setContentText("body")
            .setSmallIcon(R.drawable.notifacation_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
        val notif2 = NotificationCompat.Builder(this,CHANNEL_ID2)
            .setContentTitle("Test")
            .setContentText("This is a notification test.")
            .setSmallIcon(R.drawable.notifacation_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .build()

        //val notifManager = NotifacationManagerCompat.from(this)
        val notifManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifManager.notify(NOTIF_ID,notif)
        val notifManager2 = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notifManager2.notify(NOTIF_ID,notif2)
        binding.appBarMain.fab.setOnClickListener { view ->

            notifManager2.notify(NOTIF_ID, notif2)
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
        }

        getdata(notifManager, notif)

    }
    // end of onCreate




    fun createNotifChannel() {
        if(Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_NAME, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor= Color.RED
                enableLights(true)
            }
            val channel2 = NotificationChannel(CHANNEL_NAME2, CHANNEL_ID2, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor= Color.RED
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(channel2)
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val WEATHER_ALERT = "WEATHER_ALERT"
    }




    class UserAgentInterceptor(private val userAgent: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestWithUserAgent = originalRequest.newBuilder()
                .header("User-Agent", userAgent)
                .build()
            return chain.proceed(requestWithUserAgent)
        }
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(UserAgentInterceptor("wallothrones")) // Replace with your desired User-Agent
        .build()
//
//    val retrofit = Retrofit.Builder()
//        .baseUrl("https://api.weather.gov/alerts/active?point=$lat,$lon")
//        .client(okHttpClient) // Add the OkHttpClient to Retrofit
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun getdata(notifManager: NotificationManager, notif: Notification): Thread
    {
        noticationify(notifManager, notif)
        return Thread {
            val lat = 46.484632
            val lon = -92.008472
            val url = URL("https://api.weather.gov/alerts/active?point=$lat,$lon")
            val connection  = url.openConnection() as HttpsURLConnection

            if(connection.responseCode == HttpsURLConnection.HTTP_OK)
            {
                val inputSystem = connection.inputStream
                val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                val request = Gson().fromJson(inputStreamReader, Request::class.java)
               // updateUI(request)
                inputStreamReader.close()
                inputSystem.close()
                println("SUCCESS")
                println(request.title)
                println(request.TITLE)
                noticationify(notifManager, notif)

            }
            else
            {
              println("FAILED TO REQUEST")
                noticationify(notifManager, notif)
            }
        }
    }

    fun noticationify(notifManager: NotificationManager, notif: Notification) {
        notifManager.notify(NOTIF_ID, notif)

    }

}


