// Create a new file: app/src/main/java/com/exoticdg/weatheralerts/util/NotificationHelper.kt
package com.exoticdg.weatheralerts.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.exoticdg.weatheralerts.MainActivity // To open the app on notification click
import com.exoticdg.weatheralerts.R // Your R file for icons etc.

class NotificationHelper(private val context: Context) {

    companion object {
        private const val WEATHER_ALERTS_CHANNEL_ID = "weather_alerts_channel"
        private const val WEATHER_ALERTS_CHANNEL_NAME = "Weather Alerts"
        private const val WEATHER_ALERTS_CHANNEL_DESC = "Notifications for important weather alerts"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WEATHER_ALERTS_CHANNEL_ID,
                WEATHER_ALERTS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = WEATHER_ALERTS_CHANNEL_DESC
                // You can set other properties like lights, vibration, sound here
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendWeatherAlertNotification(title: String, message: String, alertId: String) {
        // Intent to open MainActivity when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // You could add extras to the intent if you want to navigate
            // to a specific alert within the app, e.g., intent.putExtra("alert_id_to_show", alertId)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            alertId.hashCode(), // Use alertId's hashcode for a unique request code for PendingIntent
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, WEATHER_ALERTS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Replace with your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message)) // For longer messages
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Set the intent that will fire when the user taps the notification
            .setAutoCancel(true) // Automatically removes the notification when the user taps it
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen


        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            // Using the hash code of the alertId ensures we don't post the same notification multiple times if the method is called again for the same alert.
            // However, if you want to *update* a notification for an ongoing event, use the same notificationId.
            // For new distinct alerts, ensure distinct notificationIds.
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // This check is good practice, though permissions should be handled before calling this.
                // In a real app, you wouldn't typically try to send a notification if permission isn't granted.
                // The permission request flow should happen in your Activity/Fragment.
                Log.w("NotificationHelper", "POST_NOTIFICATIONS permission not granted.")
                return
            }
            notify(alertId.hashCode(), builder.build()) // Use alertId's hashcode as notificationId
        }
    }
}
