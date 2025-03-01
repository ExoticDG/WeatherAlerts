package com.exoticdg.weatheralerts

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var isOverlayDisplayed = mutableStateOf(false)

    override fun onBind(intent: Intent?): IBinder? {
        return null // Not a bound service
    }

    override fun onCreate() {
        super.onCreate()

        // Get the window manager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isOverlayDisplayed.value) {
            showOverlay()
        } else {
            hideOverlay()
        }
        return START_NOT_STICKY
    }

    private fun showOverlay() {
        // Inflate the layout
        val inflater = LayoutInflater.from(this)
        overlayView = inflater.inflate(R.layout.overlay_layout, null)

          // Set the message if you need
         // val messageTextView = overlayView.findViewById<TextView>(R.id.overlay_message)
        // messageTextView.text = "Salve! This is a test alert!"

        // Create layout parameters
        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            getOverlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        // Position in the center
        layoutParams.gravity = Gravity.CENTER

        // Add the view to the window manager
        windowManager.addView(overlayView, layoutParams)
        isOverlayDisplayed.value = true
    }

    private fun hideOverlay() {
        windowManager.removeView(overlayView)
        isOverlayDisplayed.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isOverlayDisplayed.value) {
            hideOverlay()
        }
    }

    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }
}