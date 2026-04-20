package com.emergency.aria.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u0001!B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\b\u0010\u0018\u001a\u00020\u0015H\u0002J\b\u0010\u0019\u001a\u00020\u0015H\u0002J\u0006\u0010\u001a\u001a\u00020\u0015J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\u0006\u0010\u001d\u001a\u00020\u001eJ\u0006\u0010\u001f\u001a\u00020\u0015J\u0006\u0010 \u001a\u00020\u0015R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/emergency/aria/service/OfflineService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_receivedAlerts", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/service/OfflineService$OfflineAlert;", "bluetoothService", "Lcom/emergency/aria/bluetooth/BluetoothService;", "gson", "Lcom/google/gson/Gson;", "receivedAlerts", "Lkotlinx/coroutines/flow/SharedFlow;", "getReceivedAlerts", "()Lkotlinx/coroutines/flow/SharedFlow;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "wifiDirectService", "Lcom/emergency/aria/wifi/WiFiDirectService;", "broadcastEmergency", "", "report", "Lcom/emergency/aria/service/EmergencyPayload;", "collectBluetoothAlerts", "collectWifiAlerts", "destroy", "getDeviceId", "", "getNearbyDeviceCount", "", "startDiscovery", "stopDiscovery", "OfflineAlert", "app_debug"})
public final class OfflineService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.bluetooth.BluetoothService bluetoothService = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.wifi.WiFiDirectService wifiDirectService = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.service.OfflineService.OfflineAlert> _receivedAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.service.OfflineService.OfflineAlert> receivedAlerts = null;
    
    public OfflineService(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.service.OfflineService.OfflineAlert> getReceivedAlerts() {
        return null;
    }
    
    private final void collectBluetoothAlerts() {
    }
    
    private final void collectWifiAlerts() {
    }
    
    public final void broadcastEmergency(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.EmergencyPayload report) {
    }
    
    public final void startDiscovery() {
    }
    
    public final void stopDiscovery() {
    }
    
    public final int getNearbyDeviceCount() {
        return 0;
    }
    
    private final java.lang.String getDeviceId() {
        return null;
    }
    
    public final void destroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BQ\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u000bH\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J]\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020&H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000fR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018\u00a8\u0006("}, d2 = {"Lcom/emergency/aria/service/OfflineService$OfflineAlert;", "", "deviceId", "", "message", "latitude", "", "longitude", "riskLevel", "imageBase64", "timestamp", "", "channel", "(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/String;Ljava/lang/String;JLjava/lang/String;)V", "getChannel", "()Ljava/lang/String;", "getDeviceId", "getImageBase64", "getLatitude", "()D", "getLongitude", "getMessage", "getRiskLevel", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class OfflineAlert {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String deviceId = null;
        @org.jetbrains.annotations.Nullable
        private final java.lang.String message = null;
        private final double latitude = 0.0;
        private final double longitude = 0.0;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String riskLevel = null;
        @org.jetbrains.annotations.Nullable
        private final java.lang.String imageBase64 = null;
        private final long timestamp = 0L;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String channel = null;
        
        public OfflineAlert(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.Nullable
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
        java.lang.String imageBase64, long timestamp, @org.jetbrains.annotations.NotNull
        java.lang.String channel) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getDeviceId() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
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
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String getImageBase64() {
            return null;
        }
        
        public final long getTimestamp() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getChannel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable
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
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String component6() {
            return null;
        }
        
        public final long component7() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component8() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.service.OfflineService.OfflineAlert copy(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.Nullable
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
        java.lang.String imageBase64, long timestamp, @org.jetbrains.annotations.NotNull
        java.lang.String channel) {
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