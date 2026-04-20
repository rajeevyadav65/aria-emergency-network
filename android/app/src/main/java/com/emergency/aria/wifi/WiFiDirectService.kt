package com.emergency.aria.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.*
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class WiFiDirectService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val PORT = 8899
        const val TAG = "P2P_SERVICE"
    }

    private val p2pManager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    private var channel: WifiP2pManager.Channel? = null
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _peersFound = MutableStateFlow<List<WifiP2pDevice>>(emptyList())
    val peersFound: StateFlow<List<WifiP2pDevice>> = _peersFound.asStateFlow()

    private val _receivedPayloads = MutableSharedFlow<EmergencyPayload>()
    val receivedPayloads: SharedFlow<EmergencyPayload> = _receivedPayloads

    private var pendingPayload: EmergencyPayload? = null

    // 🟢 1. Receiver ko Object banaya taaki manifest constructor ki zarurat na pade
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> requestPeers()
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val info = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO) ?: return
                    if (info.groupFormed) {
                        if (info.isGroupOwner) startServer()
                        else info.groupOwnerAddress?.let { connectToGroupOwner(it.hostAddress) }
                    }
                }
            }
        }
    }

    data class EmergencyPayload(
        val deviceId: String,
        val message: String,
        val latitude: Double,
        val longitude: Double,
        val riskLevel: String,
        val imageBase64: String? = null,
        val timestamp: Long = System.currentTimeMillis()
    )

    fun init() {
        channel = p2pManager.initialize(context, context.mainLooper, null)
        val filter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        }
        context.registerReceiver(receiver, filter)
    }

    @SuppressLint("MissingPermission")
    fun discoverPeers() {
        channel?.let { ch ->
            p2pManager.discoverPeers(ch, object : WifiP2pManager.ActionListener {
                override fun onSuccess() { Log.d(TAG, "Discovery started") }
                override fun onFailure(reason: Int) { Log.e(TAG, "Discovery failed: $reason") }
            })
        }
    }

    @SuppressLint("MissingPermission")
    fun requestPeers() {
        channel?.let { ch ->
            p2pManager.requestPeers(ch) { peers ->
                _peersFound.value = peers.deviceList.toList()
            }
        }
    }

    fun startServer() {
        scope.launch {
            try {
                val server = ServerSocket(PORT)
                while (isActive) {
                    val client = server.accept()
                    launch { handleClient(client) }
                }
            } catch (e: Exception) { Log.e(TAG, "Server error: ${e.message}") }
        }
    }

    private suspend fun handleClient(socket: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val json = reader.readLine()
            if (json != null) {
                val payload = gson.fromJson(json, EmergencyPayload::class.java)
                _receivedPayloads.emit(payload)
            }
        } catch (e: Exception) { Log.e(TAG, "Client handle error: ${e.message}") } finally { socket.close() }
    }

    fun connectToGroupOwner(host: String?) {
        if (host == null) return
        scope.launch {
            try {
                Socket(host, PORT).use { socket ->
                    val writer = PrintWriter(socket.getOutputStream(), true)
                    pendingPayload?.let { writer.println(gson.toJson(it)) }
                }
            } catch (e: Exception) { Log.e(TAG, "Client error: ${e.message}") }
        }
    }

    fun sendPayload(payload: EmergencyPayload) {
        pendingPayload = payload
        discoverPeers()
    }

    fun destroy() {
        scope.cancel()
        try { context.unregisterReceiver(receiver) } catch (e: Exception) {}
    }
}