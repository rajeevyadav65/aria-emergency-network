package com.emergency.aria.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * LIVE LOCATION SERVICE
 * ──────────────────────
 * High-accuracy GPS tracking using Google Fused Location Provider.
 *
 * Modes:
 *   NORMAL   — 10-second updates (battery-friendly)
 *   EMERGENCY — 2-second updates (high accuracy, higher battery drain)
 *   TRACKING  — Continuous updates (for Police/Ambulance role)
 *
 * Stores last known location for OFFLINE use (Room DB via SyncManager).
 */
@Singleton
class LiveLocationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    enum class TrackingMode { NORMAL, EMERGENCY, TRACKING }

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation

    private val _lastKnownLocation = MutableStateFlow<Location?>(null)
    val lastKnownLocation: StateFlow<Location?> = _lastKnownLocation

    private var currentMode = TrackingMode.NORMAL

    // Location request presets
    private val normalRequest = LocationRequest.Builder(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10_000L
    ).setMinUpdateIntervalMillis(5_000L).build()

    private val emergencyRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 2_000L
    ).setMinUpdateIntervalMillis(1_000L).build()

    private val trackingRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1_000L
    ).setMinUpdateIntervalMillis(500L).build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            _currentLocation.value = LatLng(location.latitude, location.longitude)
            _lastKnownLocation.value = location
        }
    }

    // ── Start / Stop ──────────────────────────────────────────────────────────

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun startTracking(mode: TrackingMode = TrackingMode.NORMAL) {
        currentMode = mode
        val request = when (mode) {
            TrackingMode.NORMAL    -> normalRequest
            TrackingMode.EMERGENCY -> emergencyRequest
            TrackingMode.TRACKING  -> trackingRequest
        }
        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        android.util.Log.i(TAG, "Location tracking started: $mode")
    }

    fun stopTracking() {
        fusedClient.removeLocationUpdates(locationCallback)
        android.util.Log.i(TAG, "Location tracking stopped")
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ])
    fun upgradeToEmergencyMode() {
        if (currentMode == TrackingMode.EMERGENCY) return
        stopTracking()
        startTracking(TrackingMode.EMERGENCY)
        android.util.Log.i(TAG, "Upgraded to EMERGENCY location tracking")
    }

    /** Get current or last known location as LatLng */
    fun getLocation(): LatLng? = _currentLocation.value

    /** Format for sharing with police/ambulance */
    fun getShareableLink(): String? {
        val loc = _currentLocation.value ?: return null
        return "https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
    }

    /** Get accuracy of current fix in metres */
    fun getAccuracyMeters(): Float =
        _lastKnownLocation.value?.accuracy ?: Float.MAX_VALUE

    companion object { private const val TAG = "LiveLocationService" }
}
