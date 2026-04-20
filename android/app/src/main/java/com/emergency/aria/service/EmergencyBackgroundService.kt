package com.emergency.aria.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.emergency.aria.CommunicationManager
import com.emergency.aria.utils.FallDetector
import com.emergency.aria.utils.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class EmergencyBackgroundService : Service() {

    @Inject lateinit var commManager: CommunicationManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fallDetector: FallDetector
    private lateinit var locationHelper: LocationHelper

    companion object {
        private const val CHANNEL_ID = "aria_monitoring"
        private const val NOTIF_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, EmergencyBackgroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationHelper = LocationHelper(this)

        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification("ARIA Protection Active"))

        fallDetector = FallDetector(this) {
            Log.e("BG_SERVICE", "🚨 Fall detected in background!")
            triggerEmergency()
        }
        fallDetector.start()
    }

    private fun triggerEmergency() {
        serviceScope.launch {
            val coords = locationHelper.getCurrentLocation()

            // 🟢 FIXED: Using EmergencyPayload with all required backend fields
            val report = EmergencyPayload(
                localId = java.util.UUID.randomUUID().toString(),
                message = "🚨 Automatic Background Fall Detection",
                latitude = coords?.first ?: 23.3441,
                longitude = coords?.second ?: 85.3091,
                fallDetected = true,
                movement = "FALLING",
                userResponse = "NONE",
                deviceId = "RAJEEV_PHONE_001",
                triggeredBy = "FALL",
                riskLevel = "HIGH"
            )

            try {
                commManager.onlineService.reportEmergency(report)
                Log.i("BG_SERVICE", "Background SOS sent successfully!")
            } catch (e: Exception) {
                Log.e("BG_SERVICE", "Failed to send background SOS: ${e.message}")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Emergency Monitor", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Smart Sahayata")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        fallDetector.stop()
        serviceScope.cancel()
    }
}