package com.emergency.aria

import com.emergency.aria.service.OnlineService
import com.emergency.aria.bluetooth.BluetoothService
import com.emergency.aria.service.EmergencyPayload
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

//  Global Enum taaki har jagah accessible ho
enum class AriaCommMode { ONLINE, OFFLINE }

@Singleton
class CommunicationManager @Inject constructor(
    val onlineService: OnlineService, // 🟢 FIXED: 'private' hata diya gaya hai
    private val bluetoothService: BluetoothService
) {
    // Current communication mode (Online/Offline)
    val mode = MutableStateFlow<AriaCommMode>(AriaCommMode.ONLINE)

    //  Function jo MainActivity online fail hone par call karti hai
    fun startOfflineEmergency() {
        mode.value = AriaCommMode.OFFLINE
        android.util.Log.w("ARIA_COMM", "Switching to Offline Mode")
    }

    //  Function jo EmergencyViewModel call karta hai
    suspend fun sendEmergency(payload: EmergencyPayload) {
        android.util.Log.i("ARIA_COMM", "Processing Emergency: ${payload.localId}")
    }
}