// BootReceiver.kt — ARIA Android Boot Receiver
// Restarts the emergency monitoring foreground service after device reboot

package com.emergency.aria.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

/**
 * Receives BOOT_COMPLETED broadcast and restarts emergency monitoring.
 *
 * This ensures voice keyword detection, fall detection, and BLE scanning
 * automatically resume after the device reboots — without the user
 * needing to reopen the app.
 *
 * Registered in AndroidManifest.xml with:
 * <action android:name="android.intent.action.BOOT_COMPLETED"/>
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs: SharedPreferences =
            context.getSharedPreferences("aria_auth", Context.MODE_PRIVATE)

        // Only restart if user was previously logged in
        val hasToken = prefs.getString("token", null) != null
        val monitoringEnabled = prefs.getBoolean("monitoring_enabled", true)

        if (hasToken && monitoringEnabled) {
            android.util.Log.i("BOOT", "ARIA: restarting emergency monitoring after reboot")
            EmergencyBackgroundService.start(context)
        } else {
            android.util.Log.d("BOOT", "ARIA: monitoring not restarted (no token or disabled)")
        }
    }
}
