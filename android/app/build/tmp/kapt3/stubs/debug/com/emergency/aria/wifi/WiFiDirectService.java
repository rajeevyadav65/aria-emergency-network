package com.emergency.aria.wifi;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u0000 22\u00020\u0001:\u000223B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010&J\u0006\u0010\'\u001a\u00020$J\b\u0010(\u001a\u00020$H\u0007J\u0019\u0010)\u001a\u00020$2\u0006\u0010*\u001a\u00020+H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010,J\u0006\u0010-\u001a\u00020$J\b\u0010.\u001a\u00020$H\u0007J\u000e\u0010/\u001a\u00020$2\u0006\u00100\u001a\u00020\u000bJ\u0006\u00101\u001a\u00020$R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0010\u001a\u00020\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0012\u0010\u0013R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u00064"}, d2 = {"Lcom/emergency/aria/wifi/WiFiDirectService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_peersFound", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Landroid/net/wifi/p2p/WifiP2pDevice;", "_receivedPayloads", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lcom/emergency/aria/wifi/WiFiDirectService$EmergencyPayload;", "channel", "Landroid/net/wifi/p2p/WifiP2pManager$Channel;", "gson", "Lcom/google/gson/Gson;", "p2pManager", "Landroid/net/wifi/p2p/WifiP2pManager;", "getP2pManager", "()Landroid/net/wifi/p2p/WifiP2pManager;", "p2pManager$delegate", "Lkotlin/Lazy;", "peersFound", "Lkotlinx/coroutines/flow/StateFlow;", "getPeersFound", "()Lkotlinx/coroutines/flow/StateFlow;", "pendingPayload", "receivedPayloads", "Lkotlinx/coroutines/flow/SharedFlow;", "getReceivedPayloads", "()Lkotlinx/coroutines/flow/SharedFlow;", "receiver", "Landroid/content/BroadcastReceiver;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "connectToGroupOwner", "", "host", "", "destroy", "discoverPeers", "handleClient", "socket", "Ljava/net/Socket;", "(Ljava/net/Socket;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "init", "requestPeers", "sendPayload", "payload", "startServer", "Companion", "EmergencyPayload", "app_debug"})
public final class WiFiDirectService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    public static final int PORT = 8899;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG = "P2P_SERVICE";
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy p2pManager$delegate = null;
    @org.jetbrains.annotations.Nullable
    private android.net.wifi.p2p.WifiP2pManager.Channel channel;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<android.net.wifi.p2p.WifiP2pDevice>> _peersFound = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<android.net.wifi.p2p.WifiP2pDevice>> peersFound = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload> _receivedPayloads = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload> receivedPayloads = null;
    @org.jetbrains.annotations.Nullable
    private com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload pendingPayload;
    @org.jetbrains.annotations.NotNull
    private final android.content.BroadcastReceiver receiver = null;
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.wifi.WiFiDirectService.Companion Companion = null;
    
    @javax.inject.Inject
    public WiFiDirectService(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    private final android.net.wifi.p2p.WifiP2pManager getP2pManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<android.net.wifi.p2p.WifiP2pDevice>> getPeersFound() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload> getReceivedPayloads() {
        return null;
    }
    
    public final void init() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void discoverPeers() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void requestPeers() {
    }
    
    public final void startServer() {
    }
    
    private final java.lang.Object handleClient(java.net.Socket socket, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void connectToGroupOwner(@org.jetbrains.annotations.Nullable
    java.lang.String host) {
    }
    
    public final void sendPayload(@org.jetbrains.annotations.NotNull
    com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload payload) {
    }
    
    public final void destroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/emergency/aria/wifi/WiFiDirectService$Companion;", "", "()V", "PORT", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u000bH\u00c6\u0003JQ\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bH\u00c6\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020#H\u00d6\u0001J\t\u0010$\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006%"}, d2 = {"Lcom/emergency/aria/wifi/WiFiDirectService$EmergencyPayload;", "", "deviceId", "", "message", "latitude", "", "longitude", "riskLevel", "imageBase64", "timestamp", "", "(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/String;Ljava/lang/String;J)V", "getDeviceId", "()Ljava/lang/String;", "getImageBase64", "getLatitude", "()D", "getLongitude", "getMessage", "getRiskLevel", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
    public static final class EmergencyPayload {
        @org.jetbrains.annotations.NotNull
        private final java.lang.String deviceId = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String message = null;
        private final double latitude = 0.0;
        private final double longitude = 0.0;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String riskLevel = null;
        @org.jetbrains.annotations.Nullable
        private final java.lang.String imageBase64 = null;
        private final long timestamp = 0L;
        
        public EmergencyPayload(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.NotNull
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
        java.lang.String imageBase64, long timestamp) {
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
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String getImageBase64() {
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
        
        @org.jetbrains.annotations.Nullable
        public final java.lang.String component6() {
            return null;
        }
        
        public final long component7() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.emergency.aria.wifi.WiFiDirectService.EmergencyPayload copy(@org.jetbrains.annotations.NotNull
        java.lang.String deviceId, @org.jetbrains.annotations.NotNull
        java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.NotNull
        java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
        java.lang.String imageBase64, long timestamp) {
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