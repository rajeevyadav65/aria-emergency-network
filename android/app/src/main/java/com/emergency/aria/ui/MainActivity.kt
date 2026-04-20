package com.emergency.aria.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.emergency.aria.CommunicationManager
import com.emergency.aria.R
import com.emergency.aria.service.EmergencyPayload
import com.emergency.aria.utils.FallDetector
import com.emergency.aria.utils.LocationHelper
import com.emergency.aria.utils.VoiceTriggerHelper
import com.google.android.material.button.MaterialButton
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {

    @Inject lateinit var commManager: CommunicationManager
    private lateinit var btnSos: MaterialButton
    private lateinit var locationHelper: LocationHelper
    private lateinit var fallDetector: FallDetector
    private lateinit var voiceHelper: VoiceTriggerHelper

    private var emergencyDialog: AlertDialog? = null
    private var timerJob: Job? = null
    private var currentDoctorId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Preload Razorpay to make checkout load instantly
        Checkout.preload(applicationContext)

        locationHelper = LocationHelper(this)

        // 🟢 NAYA: Multi-Language App Setup
        checkAndAskLanguagePreference()

        voiceHelper = VoiceTriggerHelper(this) {
            triggerEmergencyProtocol("Detected via Mic Analysis")
        }
        voiceHelper.stop()

        fallDetector = FallDetector(this) {
            handleImpactDetected()
        }
        fallDetector.start()

        btnSos = findViewById(R.id.btnSos)
        btnSos.setOnClickListener { triggerEmergencyProtocol("Manual SOS") }
    }

    // 🟢 1. Multi-Language Support System
    private fun checkAndAskLanguagePreference() {
        val prefs = getSharedPreferences("ARIA_PREFS", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("LANGUAGE_SET", false)) {
            val languages = arrayOf("English", "हिन्दी (Hindi)", "বাংলা (Bengali)", "தமிழ் (Tamil)", "मराठी (Marathi)")
            val langCodes = arrayOf("en", "hi", "bn", "ta", "mr")

            AlertDialog.Builder(this)
                .setTitle("Choose Assistant Language / भाषा चुनें")
                .setCancelable(false)
                .setItems(languages) { _, which ->
                    prefs.edit().putString("LANGUAGE", langCodes[which]).putBoolean("LANGUAGE_SET", true).apply()
                    Toast.makeText(this, "Language updated to ${languages[which]}", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    // 🟢 2. Payment Integration (UPI Redirect enabled)
    fun startDoctorAppointmentPayment(doctorId: Long, doctorFee: Int) {
        currentDoctorId = doctorId
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_YOUR_KEY_HERE") // Apna Razorpay Key yahan daalein

        try {
            val options = JSONObject()
            options.put("name", "ARIA Healthcare")
            options.put("description", "Consultation Fee (Queue Booking)")
            options.put("currency", "INR")
            options.put("amount", (doctorFee * 100).toString()) // Amount in paisa

            // Forces UPI intent (GPay, PhonePe opens directly)
            val method = JSONObject()
            method.put("upi", true)
            method.put("netbanking", true)
            method.put("card", true)
            options.put("method", method)

            val preFill = JSONObject()
            preFill.put("email", "patient@gmail.com")
            preFill.put("contact", "9999999999")
            options.put("prefill", preFill)

            checkout.open(this, options)
        } catch (e: Exception) {
            Log.e("PAYMENT", "Error starting Razorpay", e)
        }
    }

    // 🟢 3. Payment Success Callback
    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Successful! Adding to Queue...", Toast.LENGTH_LONG).show()
        // API call to backend to confirm booking
    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Failed. Cannot book appointment.", Toast.LENGTH_LONG).show()
    }

    private fun handleImpactDetected() {
        lifecycleScope.launch { delay(3000); showEmergencyPopup() }
    }

    private fun showEmergencyPopup() {
        if (emergencyDialog?.isShowing == true) return
        val builder = AlertDialog.Builder(this)
        builder.setTitle("⚠️ Emergency Detected?")
        builder.setMessage("Are you in danger? Tap Yes to send SOS.")
        builder.setCancelable(false)
        builder.setPositiveButton("YES") { _, _ -> timerJob?.cancel(); triggerEmergencyProtocol("Confirmed by User") }
        builder.setNegativeButton("NO") { _, _ -> timerJob?.cancel(); voiceHelper.stop() }
        emergencyDialog = builder.create()
        emergencyDialog?.show()

        timerJob = lifecycleScope.launch {
            delay(10000)
            if (emergencyDialog?.isShowing == true) {
                emergencyDialog?.dismiss()
                voiceHelper.startListening()
            }
        }
    }

    private fun triggerEmergencyProtocol(reason: String) {
        voiceHelper.stop()
        Toast.makeText(this, "Emergency Triggered: $reason", Toast.LENGTH_LONG).show()
        lifecycleScope.launch {
            val coords = locationHelper.getCurrentLocation()
            val myDeviceId = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: "UNKNOWN"
            val report = EmergencyPayload(
                message = "Emergency: $reason",
                latitude = coords?.first ?: 0.0,
                longitude = coords?.second ?: 0.0,
                fallDetected = true,
                movement = "HIGH_IMPACT",
                userResponse = "AUTO_TRIGGERED",
                deviceId = myDeviceId
            )
            try { commManager.onlineService.reportEmergency(report) } catch (e: Exception) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        fallDetector.stop()
        voiceHelper.stop()
    }
}