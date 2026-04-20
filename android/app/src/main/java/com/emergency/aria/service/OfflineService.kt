// OfflineService.kt — ARIA Offline Mode Orchestrator
package com.emergency.aria.service

import android.content.Context
import com.emergency.aria.bluetooth.BluetoothService
import com.emergency.aria.wifi.WiFiDirectService
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class OfflineService(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val bluetoothService = BluetoothService(context)
    private val wifiDirectService = WiFiDirectService(context)
    private val gson = Gson()

    private val _receivedAlerts = MutableSharedFlow<OfflineAlert>()
    val receivedAlerts: SharedFlow<OfflineAlert> = _receivedAlerts

    data class OfflineAlert(
        val deviceId: String,
        val message: String?,
        val latitude: Double,
        val longitude: Double,
        val riskLevel: String = "HIGH",
        val imageBase64: String? = null,
        val timestamp: Long = System.currentTimeMillis(),
        val channel: String = "UNKNOWN"
    )

    init {
        collectBluetoothAlerts()
        collectWifiAlerts()
    }

    private fun collectBluetoothAlerts() {
        scope.launch {
            bluetoothService.receivedAlerts.collect { bleAlert ->
                _receivedAlerts.emit(OfflineAlert(
                    deviceId = bleAlert.deviceId,
                    message = bleAlert.message,
                    latitude = bleAlert.latitude,
                    longitude = bleAlert.longitude,
                    riskLevel = bleAlert.riskLevel,
                    channel = "BLE"
                ))
            }
        }
    }

    private fun collectWifiAlerts() {
        scope.launch {
            wifiDirectService.receivedPayloads.collect { payload ->
                _receivedAlerts.emit(OfflineAlert(
                    deviceId = payload.deviceId,
                    message = payload.message,
                    latitude = payload.latitude,
                    longitude = payload.longitude,
                    riskLevel = payload.riskLevel,
                    imageBase64 = payload.imageBase64,
                    channel = "WIFI_DIRECT"
                ))
            }
        }
    }

    // ── Send emergency to nearby peers ────────────────────────────────────────

    // 🟢 FIXED: 'EmergencyReport' ko badal kar 'EmergencyPayload' kar diya gaya hai
    fun broadcastEmergency(report: EmergencyPayload) {
        // Channel 1: BLE
        bluetoothService.startEmergencyBroadcast(BluetoothService.OfflineAlert(
            deviceId = report.deviceId,
            message = report.message ?: "Emergency",
            latitude = report.latitude,
            longitude = report.longitude,
            riskLevel = "HIGH"
        ))

        // Channel 2: WiFi Direct
        wifiDirectService.sendPayload(WiFiDirectService.EmergencyPayload(
            deviceId = report.deviceId,
            message = report.message ?: "Emergency",
            latitude = report.latitude,
            longitude = report.longitude,
            riskLevel = "HIGH"
        ))
    }

    fun startDiscovery() {
        bluetoothService.startScan()
        wifiDirectService.init()
        wifiDirectService.discoverPeers()
    }

    fun stopDiscovery() {
        bluetoothService.stopScan()
        bluetoothService.stopAdvertising()
    }

    fun getNearbyDeviceCount(): Int = wifiDirectService.peersFound.value.size

    private fun getDeviceId(): String =
        context.getSharedPreferences("aria_auth", Context.MODE_PRIVATE)
            .getString("device_id", "unknown") ?: "unknown"

    fun destroy() {
        scope.cancel()
        bluetoothService.destroy()
        wifiDirectService.destroy()
    }
}