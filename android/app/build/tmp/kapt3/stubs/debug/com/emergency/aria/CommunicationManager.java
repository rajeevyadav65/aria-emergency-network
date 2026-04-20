package com.emergency.aria;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0012J\u0006\u0010\u0013\u001a\u00020\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0014"}, d2 = {"Lcom/emergency/aria/CommunicationManager;", "", "onlineService", "Lcom/emergency/aria/service/OnlineService;", "bluetoothService", "Lcom/emergency/aria/bluetooth/BluetoothService;", "(Lcom/emergency/aria/service/OnlineService;Lcom/emergency/aria/bluetooth/BluetoothService;)V", "mode", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/emergency/aria/AriaCommMode;", "getMode", "()Lkotlinx/coroutines/flow/MutableStateFlow;", "getOnlineService", "()Lcom/emergency/aria/service/OnlineService;", "sendEmergency", "", "payload", "Lcom/emergency/aria/service/EmergencyPayload;", "(Lcom/emergency/aria/service/EmergencyPayload;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startOfflineEmergency", "app_debug"})
public final class CommunicationManager {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.service.OnlineService onlineService = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.bluetooth.BluetoothService bluetoothService = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.emergency.aria.AriaCommMode> mode = null;
    
    @javax.inject.Inject
    public CommunicationManager(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.OnlineService onlineService, @org.jetbrains.annotations.NotNull
    com.emergency.aria.bluetooth.BluetoothService bluetoothService) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.emergency.aria.service.OnlineService getOnlineService() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.MutableStateFlow<com.emergency.aria.AriaCommMode> getMode() {
        return null;
    }
    
    public final void startOfflineEmergency() {
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object sendEmergency(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.EmergencyPayload payload, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}