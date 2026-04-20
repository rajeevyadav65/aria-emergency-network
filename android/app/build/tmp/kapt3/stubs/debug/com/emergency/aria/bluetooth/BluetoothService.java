package com.emergency.aria.bluetooth;

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
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0007\u0018\u0000 ,2\u00020\u0001:\u0002,-B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\b\u0010 \u001a\u00020\u001dH\u0002J\u0010\u0010!\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020#H\u0002J\u0006\u0010$\u001a\u00020\u001dJ\u000e\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0007J\u0010\u0010\'\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0007H\u0002J\u0006\u0010(\u001a\u00020\u001dJ\r\u0010)\u001a\u0004\u0018\u00010\u001d\u00a2\u0006\u0002\u0010*J\r\u0010+\u001a\u0004\u0018\u00010\u001d\u00a2\u0006\u0002\u0010*R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_receivedAlerts", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/bluetooth/BluetoothService$OfflineAlert;", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "advertiseCallback", "Landroid/bluetooth/le/AdvertiseCallback;", "advertiser", "Landroid/bluetooth/le/BluetoothLeAdvertiser;", "gattServer", "Landroid/bluetooth/BluetoothGattServer;", "gattServerCallback", "Landroid/bluetooth/BluetoothGattServerCallback;", "gson", "Lcom/google/gson/Gson;", "receivedAlerts", "Lkotlinx/coroutines/flow/SharedFlow;", "getReceivedAlerts", "()Lkotlinx/coroutines/flow/SharedFlow;", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "scanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "advertiseEmergencyData", "", "payload", "", "advertiseServiceUuid", "connectToDevice", "device", "Landroid/bluetooth/BluetoothDevice;", "destroy", "startEmergencyBroadcast", "alert", "startGattServer", "startScan", "stopAdvertising", "()Lkotlin/Unit;", "stopScan", "Companion", "OfflineAlert", "app_debug"})
@android.annotation.SuppressLint(value = {"MissingPermission"})
public final class BluetoothService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.UUID ARIA_SERVICE_UUID = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.UUID EMERGENCY_CHAR_UUID = null;
    @org.jetbrains.annotations.NotNull
    private static final java.util.UUID LOCATION_CHAR_UUID = null;
    @org.jetbrains.annotations.Nullable
    private final android.bluetooth.BluetoothAdapter adapter = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattServer gattServer;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.le.BluetoothLeScanner scanner;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.le.BluetoothLeAdvertiser advertiser;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.bluetooth.BluetoothService.OfflineAlert> _receivedAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.bluetooth.BluetoothService.OfflineAlert> receivedAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.BluetoothGattServerCallback gattServerCallback = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.AdvertiseCallback advertiseCallback = null;
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.bluetooth.BluetoothService.Companion Companion = null;
    
    public BluetoothService(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.bluetooth.BluetoothService.OfflineAlert> getReceivedAlerts() {
        return null;
    }
    
    public final void startEmergencyBroadcast(@org.jetbrains.annotations.NotNull
    com.emergency.aria.bluetooth.BluetoothService.OfflineAlert alert) {
    }
    
    private final void advertiseEmergencyData(byte[] payload) {
    }
    
    private final void advertiseServiceUuid() {
    }
    
    private final void startGattServer(com.emergency.aria.bluetooth.BluetoothService.OfflineAlert alert) {
    }
    
    public final void startScan() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit stopScan() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final kotlin.Unit stopAdvertising() {
        return null;
    }
    
    private final void connectToDevice(android.bluetooth.BluetoothDevice device) {
    }
    
    public final void destroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006\u00a8\u0006\u000b"}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothService$Companion;", "", "()V", "ARIA_SERVICE_UUID", "Ljava/util/UUID;", "getARIA_SERVICE_UUID", "()Ljava/util/UUID;", "EMERGENCY_CHAR_UUID", "getEMERGENCY_CHAR_UUID", "LOCATION_CHAR_UUID", "getLOCATION_CHAR_UUID", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.UUID getARIA_SERVICE_UUID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.UUID getEMERGENCY_CHAR_UUID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.util.UUID getLOCATION_CHAR_UUID() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\nH\u00c6\u0003JE\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001f\u001a\u00020 H\u00d6\u0001J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u0006\""}, d2 = {"Lcom/emergency/aria/bluetooth/BluetoothService$OfflineAlert;", "", "deviceId", "", "message", "latitude", "", "longitude", "riskLevel", "timestamp", "", "(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/String;J)V", "getDeviceId", "()Ljava/lang/String;", "getLatitude", "()D", "getLongitude", "getMessage", "getRiskLevel", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class OfflineAlert {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String deviceId = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String message = null;
        private final double latitude = 0.0;
        private final double longitude = 0.0;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String riskLevel = null;
        private final long timestamp = 0L;
        
        public OfflineAlert(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.NotNull
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, long timestamp) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getDeviceId() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getMessage() {
            return null;
        }
        
        public final double getLatitude() {
            return 0.0;
        }
        
        public final double getLongitude() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getRiskLevel() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component2() {
            return null;
        }
        
        public final double component3() {
            return 0.0;
        }
        
        public final double component4() {
            return 0.0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component5() {
            return null;
        }
        
        public final long component6() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.bluetooth.BluetoothService.OfflineAlert copy(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.NotNull
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, long timestamp) {
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
}