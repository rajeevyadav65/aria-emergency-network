// BluetoothService.kt — ARIA Offline Mode
// BLE GATT-based peer-to-peer emergency broadcasting
// Works without internet, SIM card, or WiFi

package com.emergency.aria.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.ParcelUuid
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

/**
 * BLE-based offline emergency broadcasting.
 *
 * HOW IT WORKS:
 * 1. ADVERTISE: Device broadcasts a custom BLE advertisement with emergency payload
 * 2. SCAN: Nearby devices (within ~100m) receive the advertisement
 * 3. CONNECT: For full payloads, connect via GATT and read the emergency characteristic
 *
 * No internet required. Range: ~100m line-of-sight.
 */
@SuppressLint("MissingPermission")
class BluetoothService(private val context: Context) {

    companion object {
        val ARIA_SERVICE_UUID: UUID = UUID.fromString("0000A1A1-0000-1000-8000-00805F9B34FB")
        val EMERGENCY_CHAR_UUID: UUID = UUID.fromString("0000E11E-0000-1000-8000-00805F9B34FB")
        val LOCATION_CHAR_UUID: UUID  = UUID.fromString("000010CA-0000-1000-8000-00805F9B34FB")
    }

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val gson = Gson()
    private var gattServer: BluetoothGattServer? = null
    private var scanner: BluetoothLeScanner? = null
    private var advertiser: BluetoothLeAdvertiser? = null

    private val _receivedAlerts = MutableSharedFlow<OfflineAlert>()
    val receivedAlerts: SharedFlow<OfflineAlert> = _receivedAlerts

    data class OfflineAlert(
        val deviceId: String,
        val message: String,
        val latitude: Double,
        val longitude: Double,
        val riskLevel: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    // ── Advertising (this device is in emergency) ─────────────────────────────

    fun startEmergencyBroadcast(alert: OfflineAlert) {
        val data = gson.toJson(alert)
        if (data.length > 512) {
            startGattServer(alert)
            advertiseServiceUuid()
        } else {
            advertiseEmergencyData(data.toByteArray())
        }
    }

    private fun advertiseEmergencyData(payload: ByteArray) {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(false)
            .setTimeout(0)
            .build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(ARIA_SERVICE_UUID))
            .addServiceData(ParcelUuid(ARIA_SERVICE_UUID),
                payload.take(20).toByteArray())
            .setIncludeDeviceName(false)
            .build()

        advertiser = adapter?.bluetoothLeAdvertiser
        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    private fun advertiseServiceUuid() {
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTimeout(0).build()

        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(ARIA_SERVICE_UUID))
            .setIncludeDeviceName(true).build()

        advertiser?.startAdvertising(settings, data, advertiseCallback)
    }

    private fun startGattServer(alert: OfflineAlert) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        gattServer = bluetoothManager.openGattServer(context, gattServerCallback)

        val service = BluetoothGattService(ARIA_SERVICE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY)

        val emergencyChar = BluetoothGattCharacteristic(
            EMERGENCY_CHAR_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ)
        emergencyChar.value = gson.toJson(alert).toByteArray()

        service.addCharacteristic(emergencyChar)
        gattServer?.addService(service)
    }

    // ── Scanning (looking for nearby emergencies) ──────────────────────────────

    fun startScan() {
        val filters = listOf(ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(ARIA_SERVICE_UUID))
            .build())

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner = adapter?.bluetoothLeScanner
        scanner?.startScan(filters, settings, scanCallback)
    }

    fun stopScan() = scanner?.stopScan(scanCallback)
    fun stopAdvertising() = advertiser?.stopAdvertising(advertiseCallback)

    // ── Callbacks ─────────────────────────────────────────────────────────────

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val serviceData = result.scanRecord
                ?.getServiceData(ParcelUuid(ARIA_SERVICE_UUID)) ?: return

            try {
                val alert = gson.fromJson(String(serviceData), OfflineAlert::class.java)
                _receivedAlerts.tryEmit(alert)
            } catch (e: Exception) {
                if (serviceData.isEmpty()) connectToDevice(result.device)
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) gatt.discoverServices()
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val char = gatt.getService(ARIA_SERVICE_UUID)
                    ?.getCharacteristic(EMERGENCY_CHAR_UUID) ?: return
                gatt.readCharacteristic(char)
            }
            override fun onCharacteristicRead(gatt: BluetoothGatt,
                                              characteristic: BluetoothGattCharacteristic,
                                              status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    try {
                        val alert = gson.fromJson(
                            String(characteristic.value), OfflineAlert::class.java)
                        _receivedAlerts.tryEmit(alert)
                    } catch (_: Exception) {}
                }
                gatt.disconnect()
            }
        })
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int,
                                                 offset: Int, characteristic: BluetoothGattCharacteristic) {
            gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS,
                offset, characteristic.value)
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            android.util.Log.i("BLE", "Emergency advertisement started")
        }
        override fun onStartFailure(errorCode: Int) {
            android.util.Log.e("BLE", "Advertisement failed: $errorCode")
        }
    }

    fun destroy() {
        stopScan(); stopAdvertising()
        gattServer?.close()
    }
}