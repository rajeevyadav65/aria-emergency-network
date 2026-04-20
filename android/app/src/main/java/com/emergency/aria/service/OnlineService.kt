package com.emergency.aria.service

import retrofit2.http.Body
import retrofit2.http.POST

interface OnlineService {
    @POST("/api/emergency/report")
    suspend fun reportEmergency(@Body report: EmergencyPayload): EmergencyResponse
}

data class EmergencyResponse(
    val emergencyId: Long? = null,
    val riskLevel: String = "UNKNOWN",
    val nearbyAlerted: Int = 0
)