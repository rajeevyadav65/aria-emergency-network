package com.emergency.aria.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emergency.aria.CommunicationManager
import com.emergency.aria.service.EmergencyPayload
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val communicationManager: CommunicationManager
) : ViewModel() {

    fun sendEmergency(lat: Double, lon: Double, triggeredBy: String) {
        val payload = EmergencyPayload(
            localId = java.util.UUID.randomUUID().toString(),
            message = "SOS Triggered: $triggeredBy",
            latitude = lat,
            longitude = lon,
            deviceId = "USER_DEVICE",
            fallDetected = triggeredBy == "FALL",
            movement = "STATIONARY",
            userResponse = "HELP",
            triggeredBy = triggeredBy,
            riskLevel = "HIGH"
        )
        viewModelScope.launch {
            // 🚀 FIXED: Ab CommunicationManager mein ye function available hai
            communicationManager.sendEmergency(payload)
        }
    }
}