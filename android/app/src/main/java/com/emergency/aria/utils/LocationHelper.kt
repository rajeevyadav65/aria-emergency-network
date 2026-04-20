package com.emergency.aria.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.CurrentLocationRequest
import kotlinx.coroutines.tasks.await // 🟢 Ye import 'tasks' aur 'await' error fix karega

class LocationHelper(private val context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        return try {
            // Priority aur Request define karein taaki compiler confusion na ho
            val locationRequest = CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            // 🟢 FIXED: Location type explicitly bataya hai
            val location: Location? = fusedLocationClient.getCurrentLocation(locationRequest, null).await()

            location?.let {
                it.latitude to it.longitude
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationHelper", "Error: ${e.message}")
            null
        }
    }
}