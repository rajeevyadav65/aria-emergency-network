package com.emergency.aria.wifi;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 )2\u00020\u0001:\u0001)B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0007H\u0007J\b\u0010\u001e\u001a\u00020\u001cH\u0003J\u0010\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J\u0019\u0010\"\u001a\u00020\u001c2\u0006\u0010#\u001a\u00020$H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010%J\b\u0010&\u001a\u00020\u001cH\u0007J\b\u0010\'\u001a\u00020\u001cH\u0002J\u0006\u0010(\u001a\u00020\u001cR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006*"}, d2 = {"Lcom/emergency/aria/wifi/WiFiDirectEmergencyService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_peerAlerts", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "", "channel", "Landroid/net/wifi/p2p/WifiP2pManager$Channel;", "groupOwnerAddress", "gson", "Lcom/google/gson/Gson;", "intentFilter", "Landroid/content/IntentFilter;", "isGroupOwner", "", "manager", "Landroid/net/wifi/p2p/WifiP2pManager;", "peerAlerts", "Lkotlinx/coroutines/flow/SharedFlow;", "getPeerAlerts", "()Lkotlinx/coroutines/flow/SharedFlow;", "receiver", "Landroid/content/BroadcastReceiver;", "serverSocket", "Ljava/net/ServerSocket;", "connectToPeer", "", "deviceAddress", "discoverPeers", "handleClient", "socket", "Ljava/net/Socket;", "sendEmergency", "payload", "Lcom/emergency/aria/service/EmergencyPayload;", "(Lcom/emergency/aria/service/EmergencyPayload;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "start", "startServer", "stop", "Companion", "app_debug"})
public final class WiFiDirectEmergencyService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    public static final int PORT = 8888;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String TAG = "WiFiDirect";
    @org.jetbrains.annotations.NotNull
    private final android.net.wifi.p2p.WifiP2pManager manager = null;
    @org.jetbrains.annotations.NotNull
    private final android.net.wifi.p2p.WifiP2pManager.Channel channel = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.Nullable
    private java.net.ServerSocket serverSocket;
    private boolean isGroupOwner = false;
    @org.jetbrains.annotations.Nullable
    private java.lang.String groupOwnerAddress;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableSharedFlow<java.lang.String> _peerAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.SharedFlow<java.lang.String> peerAlerts = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.IntentFilter intentFilter = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.BroadcastReceiver receiver = null;
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.wifi.WiFiDirectEmergencyService.Companion Companion = null;
    
    @javax.inject.Inject
    public WiFiDirectEmergencyService(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.String> getPeerAlerts() {
        return null;
    }
    
    @androidx.annotation.RequiresPermission(allOf = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.NEARBY_WIFI_DEVICES"})
    public final void start() {
    }
    
    public final void stop() {
    }
    
    @androidx.annotation.RequiresPermission(allOf = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.NEARBY_WIFI_DEVICES"})
    private final void discoverPeers() {
    }
    
    @androidx.annotation.RequiresPermission(value = "android.permission.NEARBY_WIFI_DEVICES")
    public final void connectToPeer(@org.jetbrains.annotations.NotNull
    java.lang.String deviceAddress) {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object sendEmergency(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.EmergencyPayload payload, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void startServer() {
    }
    
    private final void handleClient(java.net.Socket socket) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/emergency/aria/wifi/WiFiDirectEmergencyService$Companion;", "", "()V", "PORT", "", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}