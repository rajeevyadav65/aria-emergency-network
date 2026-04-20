package com.emergency.aria.bluetooth

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import androidx.annotation.RequiresPermission
import com.emergency.aria.service.EmergencyPayload
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * BLUETOOTH LE EMERGENCY SERVICE
 * ────────────────────────────────
 * Broadcasts emergency alerts to nearby Android devices via BLE advertising.
 * No pairing required — uses BLE beacons with a custom service UUID.
 *
 * Range: ~30–100 metres depending on hardware and obstacles.
 *
 * PROTOCOL:
 *   Advertise: SERVICE_UUID with emergency JSON payload in manufacturer data
 *   Scan:      Listen for our SERVICE_UUID and decode incoming alerts
 */
@Singleton
class BluetoothEmergencyService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        // Custom UUID for ARIA emergency service — unique to this app
        val SERVICE_UUID: UUID = UUID.fromString("0000ARIA-0000-1000-8000-00805F9B34FB")
        val CHAR_UUID: UUID    = UUID.fromString("0000ARIA-0001-1000-8000-00805F9B34FB")
        const val MANUFACTURER_ID = 0x0042  // ARIA Emergency Network
        const val TAG = "BluetoothEmergency"
    }

    private val btManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val btAdapter: BluetoothAdapter? = btManager.adapter
    private var advertiser: BluetoothLeAdvertiser? = null
    private var scanner: BluetoothLeScanner?    = null
    private val gson = Gson()

    private val _receivedAlerts = MutableSharedFlow<RemoteEmergencyAlert>(replay = 5)
    val receivedAlerts: SharedFlow<RemoteEmergencyAlert> = _receivedAlerts

    private var isRunning = false

    // ── Start / Stop ──────────────────────────────────────────────────────────

    @RequiresPermission(allOf = [
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_SCAN
    ])
    fun start() {
        if (isRunning || btAdapter == null || !btAdapter.isEnabled) return
        startScanning()
        isRunning = true
        android.util.Log.i(TAG, "BLE service started — scanning for emergency alerts")
    }

    @RequiresPermission(allOf = [
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_SCAN
    ])
    fun stop() {
        if (!isRunning) return
        advertiser?.stopAdvertising(advertiseCallback)
        scanner?.stopScan(scanCallback)
        isRunning = false
        android.util.Log.i(TAG, "BLE service stopped")
    }

    // ── Broadcast emergency (advertise) ───────────────────────────────────────

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun broadcastEmergency(payload: EmergencyPayload) {
        advertiser = btAdapter?.bluetoothLeAdvertiser ?: return

        // Encode payload — keep under 31 bytes for BLE advertising
        val compact = CompactEmergency(
            id  = payload.localId.takeLast(8),
            lat = payload.latitude,
            lon = payload.longitude,
            rl  = "H",  // H=HIGH, M=MEDIUM, L=LOW
            msg = (payload.message ?: "SOS").take(16)
        )
        val json  = gson.toJson(compact)
        val bytes = json.toByteArray(Charsets.UTF_8).take(26).toByteArray()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(30_000)  // broadcast for 30 seconds
            .build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .addManufacturerData(MANUFACTURER_ID, bytes)
            .setIncludeDeviceName(false)
            .build()

        advertiser!!.startAdvertising(settings, data, advertiseCallback)
        android.util.Log.i(TAG, "Broadcasting emergency via BLE: ${compact.msg}")
    }

    // ── Scan for nearby emergencies ───────────────────────────────────────────

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun startScanning() {
        scanner = btAdapter?.bluetoothLeScanner ?: return

        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .build()

        scanner!!.startScan(listOf(filter), settings, scanCallback)
    }

    // ── Callbacks ─────────────────────────────────────────────────────────────

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            android.util.Log.i(TAG, "BLE advertising started")
        }
        override fun onStartFailure(errorCode: Int) {
            android.util.Log.e(TAG, "BLE advertising failed: $errorCode")
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val mfData = result.scanRecord
                ?.getManufacturerSpecificData(MANUFACTURER_ID) ?: return
            try {
                val json    = String(mfData, Charsets.UTF_8)
                val compact = gson.fromJson(json, CompactEmergency::class.java)
                val alert   = RemoteEmergencyAlert(
                    deviceAddress = result.device.address,
                    rssi          = result.rssi,
                    latitude      = compact.lat,
                    longitude     = compact.lon,
                    message       = compact.msg,
                    riskLevel     = when (compact.rl) { "H" -> "HIGH" "M" -> "MEDIUM" else -> "LOW" }
                )
                // Emit on shared flow (collected in ViewModel)
                kotlinx.coroutines.runBlocking {
                    _receivedAlerts.emit(alert)
                }
                android.util.Log.i(TAG,
                    "Received BLE emergency from ${result.device.address}: ${compact.msg}")
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to parse BLE payload: ${e.message}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            android.util.Log.e(TAG, "BLE scan failed: $errorCode")
        }
    }

    // ── Data classes ─────────────────────────────────────────────────────────

    /** Compact BLE payload — fits in 31-byte advertising packet */
    data class CompactEmergency(
        val id: String, val lat: Double, val lon: Double,
        val rl: String, val msg: String
    )
}

data class RemoteEmergencyAlert(
    val deviceAddress: String,
    val rssi: Int,
    val latitude: Double,
    val longitude: Double,
    val message: String,
    val riskLevel: String,
    val receivedAt: Long = System.currentTimeMillis()
)
