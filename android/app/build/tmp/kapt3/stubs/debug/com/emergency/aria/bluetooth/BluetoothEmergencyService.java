package com.emergency.aria.bluetooth;

/**
 * BLUETOOTH LE EMERGENCY SERVICE
 * ────────────────────────────────
 * Broadcasts emergency alerts to nearby Android devices via BLE advertising.
 * No pairing required — uses BLE beacons with a custom service UUID.
 *
 * Range: ~30–100 metres depending on hardware and obstacles.
 *
 * PROTOCOL:
 *  Advertise: SERVICE_UUID with emergency JSON payload in manufacturer data
 *  Scan:      Listen for our SERVICE_UUID and decode incoming alerts
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 $2\u00020\u0001:\u0002#$B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0007J\b\u0010 \u001a\u00020\u001dH\u0007J\b\u0010!\u001a\u00020\u001dH\u0003J\b\u0010\"\u001a\u00020\u001dH\u0007R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006%"}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothEmergencyService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_receivedAlerts", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/bluetooth/RemoteEmergencyAlert;", "advertiseCallback", "Landroid/bluetooth/le/AdvertiseCallback;", "advertiser", "Landroid/bluetooth/le/BluetoothLeAdvertiser;", "btAdapter", "Landroid/bluetooth/BluetoothAdapter;", "btManager", "Landroid/bluetooth/BluetoothManager;", "gson", "Lcom/google/gson/Gson;", "isRunning", "", "receivedAlerts", "Lkotlinx/coroutines/flow/SharedFlow;", "getReceivedAlerts", "()Lkotlinx/coroutines/flow/SharedFlow;", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "scanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "broadcastEmergency", "", "payload", "Lcom/emergency/aria/service/EmergencyPayload;", "start", "startScanning", "stop", "CompactEmergency", "Companion", "app_debug"})
public final class BluetoothEmergencyService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.UUID SERVICE_UUID = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.UUID CHAR_UUID = null;
    public static final int MANUFACTURER_ID = 66;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG = "BluetoothEmergency";
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.BluetoothManager btManager = null;
    @org.jetbrains.annotations.Nullable
    private final android.bluetooth.BluetoothAdapter btAdapter = null;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.le.BluetoothLeAdvertiser advertiser;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.le.BluetoothLeScanner scanner;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.bluetooth.RemoteEmergencyAlert> _receivedAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.bluetooth.RemoteEmergencyAlert> receivedAlerts = null;
    private boolean isRunning = false;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.AdvertiseCallback advertiseCallback = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.bluetooth.BluetoothEmergencyService.Companion Companion = null;
    
    @javax.inject.Inject
    public BluetoothEmergencyService(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.bluetooth.RemoteEmergencyAlert> getReceivedAlerts() {
        return null;
    }
    
    @androidx.annotation.RequiresPermission(allOf = {"android.permission.BLUETOOTH_ADVERTISE", "android.permission.BLUETOOTH_SCAN"})
    public final void start() {
    }
    
    @androidx.annotation.RequiresPermission(allOf = {"android.permission.BLUETOOTH_ADVERTISE", "android.permission.BLUETOOTH_SCAN"})
    public final void stop() {
    }
    
    @androidx.annotation.RequiresPermission(value = "android.permission.BLUETOOTH_ADVERTISE")
    public final void broadcastEmergency(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.EmergencyPayload payload) {
    }
    
    @androidx.annotation.RequiresPermission(value = "android.permission.BLUETOOTH_SCAN")
    private final void startScanning() {
    }
    
    /**
     * Compact BLE payload — fits in 31-byte advertising packet
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\u001bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001d"}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothEmergencyService$CompactEmergency;", "", "id", "", "lat", "", "lon", "rl", "msg", "(Ljava/lang/String;DDLjava/lang/String;Ljava/lang/String;)V", "getId", "()Ljava/lang/String;", "getLat", "()D", "getLon", "getMsg", "getRl", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class CompactEmergency {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String id = null;
        private final double lat = 0.0;
        private final double lon = 0.0;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String rl = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String msg = null;
        
        public CompactEmergency(@org.jetbrains.annotations.NotNull
        java.lang.String id, double lat, double lon, @org.jetbrains.annotations.NotNull
        java.lang.String rl, @org.jetbrains.annotations.NotNull
        java.lang.String msg) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getId() {
            return null;
        }
        
        public final double getLat() {
            return 0.0;
        }
        
        public final double getLon() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getRl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getMsg() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component1() {
            return null;
        }
        
        public final double component2() {
            return 0.0;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.bluetooth.BluetoothEmergencyService.CompactEmergency copy(@org.jetbrains.annotations.NotNull
        java.lang.String id, double lat, double lon, @org.jetbrains.annotations.NotNull
        java.lang.String rl, @org.jetbrains.annotations.NotNull
        java.lang.String msg) {
            return null;
        }
        
        @java.lang.Override
        public boolean equals(@org.jetbrains.annotations.Nullable
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override
        @org.jetbrains.annotations.NotNull
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006R\u000e\u0010\u000b\u001a\u00020\fX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothEmergencyService$Companion;", "", "()V", "CHAR_UUID", "Ljava/util/UUID;", "getCHAR_UUID", "()Ljava/util/UUID;", "MANUFACTURER_ID", "", "SERVICE_UUID", "getSERVICE_UUID", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.UUID getSERVICE_UUID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.UUID getCHAR_UUID() {
            return null;
        }
    }
}