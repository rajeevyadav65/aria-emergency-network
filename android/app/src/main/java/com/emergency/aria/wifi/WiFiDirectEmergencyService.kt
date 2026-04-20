package com.emergency.aria.wifi

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.*
import android.util.Log
import androidx.annotation.RequiresPermission
import com.emergency.aria.service.EmergencyPayload
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WiFiDirectEmergencyService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val PORT = 8888
        const val TAG  = "WiFiDirect"
    }

    private val manager: WifiP2pManager =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel: WifiP2pManager.Channel =
        manager.initialize(context, context.mainLooper, null)
    private val gson = Gson()

    private var serverSocket: ServerSocket? = null
    private var isGroupOwner = false
    private var groupOwnerAddress: String? = null

    private val _peerAlerts = MutableSharedFlow<String>(replay = 10)
    val peerAlerts: SharedFlow<String> = _peerAlerts

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private val receiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        override fun onReceive(ctx: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    @Suppress("DEPRECATION")
                    val networkInfo = intent.getParcelableExtra<android.net.NetworkInfo>(
                        WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo?.isConnected == true) {
                        manager.requestConnectionInfo(channel) { info ->
                            isGroupOwner       = info.isGroupOwner
                            groupOwnerAddress  = info.groupOwnerAddress?.hostAddress
                            Log.i(TAG, "P2P connected. GroupOwner=$isGroupOwner addr=$groupOwnerAddress")
                            if (isGroupOwner) startServer()
                        }
                    }
                }
            }
        }
    }

    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES
    ])
    fun start() {
        context.registerReceiver(receiver, intentFilter)
        discoverPeers()
        Log.i(TAG, "WiFi Direct service started")
    }

    fun stop() {
        runCatching { context.unregisterReceiver(receiver) }
        serverSocket?.close()
        manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {}
            override fun onFailure(reason: Int) {}
        })
    }

    @RequiresPermission(allOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.NEARBY_WIFI_DEVICES
    ])
    private fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess(): Unit {
                Log.i(TAG, "Peer discovery started")
            }
            override fun onFailure(reason: Int): Unit {
                Log.w(TAG, "Peer discovery failed: $reason")
            }
        })
    }

    @RequiresPermission(Manifest.permission.NEARBY_WIFI_DEVICES)
    fun connectToPeer(deviceAddress: String) {
        val config = WifiP2pConfig().apply {
            this.deviceAddress = deviceAddress
            wps.setup = WpsInfo.PBC
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess(): Unit {
                Log.i(TAG, "Connecting to $deviceAddress")
            }
            override fun onFailure(reason: Int): Unit {
                Log.e(TAG, "Connect failed: $reason")
            }
        })
    }

    suspend fun sendEmergency(payload: EmergencyPayload) = withContext(Dispatchers.IO) {
        val targetAddress = groupOwnerAddress ?: run {
            Log.w(TAG, "No P2P connection established")
            return@withContext
        }
        try {
            Socket(targetAddress, PORT).use { socket ->
                val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())))
                writer.println(gson.toJson(payload))
                writer.flush()
                Log.i(TAG, "Emergency sent via WiFi Direct to $targetAddress")
            }
        } catch (e: IOException) {
            Log.e(TAG, "WiFi Direct send failed: ${e.message}")
        }
    }

    private fun startServer() {
        Thread {
            try {
                serverSocket = ServerSocket(PORT)
                Log.i(TAG, "P2P server listening on port $PORT")
                while (!serverSocket!!.isClosed) {
                    val client = serverSocket!!.accept()
                    handleClient(client)
                }
            } catch (e: IOException) {
                Log.w(TAG, "Server closed: ${e.message}")
            }
        }.start()
    }

    private fun handleClient(socket: Socket) {
        Thread {
            try {
                socket.use {
                    val reader = BufferedReader(InputStreamReader(it.getInputStream()))
                    val json = reader.readLine()
                    Log.i(TAG, "Received P2P emergency: $json")
                    if (json != null) {
                        kotlinx.coroutines.runBlocking { _peerAlerts.emit(json) }
                    }
                }
            } catch (e: IOException) {
                Log.w(TAG, "Client read error: ${e.message}")
            }
        }.start()
    }
}