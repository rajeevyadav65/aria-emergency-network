package com.emergency.aria.service

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyServiceManager @Inject constructor(
    private val onlineService: OnlineService
) {
    // Ye class sirf backend specific tasks ke liye use karein
    fun getServiceStatus(): String = "Service is Active"
}