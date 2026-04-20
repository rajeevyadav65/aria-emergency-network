package com.emergency.aria.service

import androidx.annotation.Keep

@Keep
data class EmergencyPayload(
    val localId: String = java.util.UUID.randomUUID().toString(),
    val message: String?,
    val latitude: Double,
    val longitude: Double,
    val fallDetected: Boolean,
    val movement: String,
    // 🟢 FIXED: Default values added for Backend compatibility
    val userResponse: String = "NONE",
    val deviceId: String = "RAJEEV_PHONE_001",
    val triggeredBy: String = "MANUAL",
    val riskLevel: String = "HIGH"
)