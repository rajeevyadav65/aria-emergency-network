package com.emergency.aria.location;

/**
 * LIVE LOCATION SERVICE
 * ──────────────────────
 * High-accuracy GPS tracking using Google Fused Location Provider.
 *
 * Modes:
 *  NORMAL   — 10-second updates (battery-friendly)
 *  EMERGENCY — 2-second updates (high accuracy, higher battery drain)
 *  TRACKING  — Continuous updates (for Police/Ambulance role)
 *
 * Stores last known location for OFFLINE use (Room DB via SyncManager).
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 $2\u00020\u0001:\u0002$%B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u001a\u001a\u00020\u001bJ\b\u0010\u001c\u001a\u0004\u0018\u00010\u0007J\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eJ\u0012\u0010\u001f\u001a\u00020 2\b\b\u0002\u0010!\u001a\u00020\u000fH\u0007J\u0006\u0010\"\u001a\u00020 J\b\u0010#\u001a\u00020 H\u0007R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\rR\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/emergency/aria/location/LiveLocationService;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_currentLocation", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/google/android/gms/maps/model/LatLng;", "_lastKnownLocation", "Landroid/location/Location;", "currentLocation", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentLocation", "()Lkotlinx/coroutines/flow/StateFlow;", "currentMode", "Lcom/emergency/aria/location/LiveLocationService$TrackingMode;", "emergencyRequest", "Lcom/google/android/gms/location/LocationRequest;", "fusedClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "lastKnownLocation", "getLastKnownLocation", "locationCallback", "Lcom/google/android/gms/location/LocationCallback;", "normalRequest", "trackingRequest", "getAccuracyMeters", "", "getLocation", "getShareableLink", "", "startTracking", "", "mode", "stopTracking", "upgradeToEmergencyMode", "Companion", "TrackingMode", "app_debug"})
public final class LiveLocationService {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.android.gms.location.FusedLocationProviderClient fusedClient = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.google.android.gms.maps.model.LatLng> _currentLocation = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.google.android.gms.maps.model.LatLng> currentLocation = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<android.location.Location> _lastKnownLocation = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<android.location.Location> lastKnownLocation = null;
    @org.jetbrains.annotations.NotNull
    private com.emergency.aria.location.LiveLocationService.TrackingMode currentMode = com.emergency.aria.location.LiveLocationService.TrackingMode.NORMAL;
    @org.jetbrains.annotations.NotNull
    private final com.google.android.gms.location.LocationRequest normalRequest = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.android.gms.location.LocationRequest emergencyRequest = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.android.gms.location.LocationRequest trackingRequest = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.android.gms.location.LocationCallback locationCallback = null;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "LiveLocationService";
    @org.jetbrains.annotations.NotNull
    public static final com.emergency.aria.location.LiveLocationService.Companion Companion = null;
    
    @javax.inject.Inject
    public LiveLocationService(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.google.android.gms.maps.model.LatLng> getCurrentLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<android.location.Location> getLastKnownLocation() {
        return null;
    }
    
    @androidx.annotation.RequiresPermission(anyOf = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"})
    public final void startTracking(@org.jetbrains.annotations.NotNull
    com.emergency.aria.location.LiveLocationService.TrackingMode mode) {
    }
    
    public final void stopTracking() {
    }
    
    @androidx.annotation.RequiresPermission(anyOf = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"})
    public final void upgradeToEmergencyMode() {
    }
    
    /**
     * Get current or last known location as LatLng
     */
    @org.jetbrains.annotations.Nullable
    public final com.google.android.gms.maps.model.LatLng getLocation() {
        return null;
    }
    
    /**
     * Format for sharing with police/ambulance
     */
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getShareableLink() {
        return null;
    }
    
    /**
     * Get accuracy of current fix in metres
     */
    public final float getAccuracyMeters() {
        return 0.0F;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/emergency/aria/location/LiveLocationService$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/emergency/aria/location/LiveLocationService$TrackingMode;", "", "(Ljava/lang/String;I)V", "NORMAL", "EMERGENCY", "TRACKING", "app_debug"})
    public static enum TrackingMode {
        /*public static final*/ NORMAL /* = new NORMAL() */,
        /*public static final*/ EMERGENCY /* = new EMERGENCY() */,
        /*public static final*/ TRACKING /* = new TRACKING() */;
        
        TrackingMode() {
        }
        
        @org.jetbrains.annotations.NotNull
        public static kotlin.enums.EnumEntries<com.emergency.aria.location.LiveLocationService.TrackingMode> getEntries() {
            return null;
        }
    }
}