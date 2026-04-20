package com.emergency.aria.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010\t\n\u0002\b*\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0097\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0012\u00a2\u0006\u0002\u0010\u0014J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u0010-\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010.\u001a\u0004\u0018\u00010\u0012H\u00c6\u0003\u00a2\u0006\u0002\u0010%J\t\u0010/\u001a\u00020\u0012H\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u00101\u001a\u00020\u0006H\u00c6\u0003J\t\u00102\u001a\u00020\u0006H\u00c6\u0003J\u0010\u00103\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001bJ\u000b\u00104\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u000b\u00105\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\t\u00106\u001a\u00020\u0003H\u00c6\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u00aa\u0001\u00108\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0010\u001a\u00020\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u0012H\u00c6\u0001\u00a2\u0006\u0002\u00109J\u0013\u0010:\u001a\u00020\t2\b\u0010;\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010<\u001a\u00020=H\u00d6\u0001J\t\u0010>\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u0013\u001a\u00020\u00128\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0015\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010\u001c\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001eR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0016R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u0016R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0016R\u0015\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\n\n\u0002\u0010&\u001a\u0004\b$\u0010%R\u0016\u0010\u0010\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u0016R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0016R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0016\u00a8\u0006?"}, d2 = {"Lcom/emergency/aria/db/OfflineEmergency;", "", "localId", "", "message", "latitude", "", "longitude", "fallDetected", "", "movement", "userResponse", "deviceId", "capturedImagePath", "triggeredBy", "riskLevel", "syncStatus", "serverEmergencyId", "", "createdOfflineAt", "(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;J)V", "getCapturedImagePath", "()Ljava/lang/String;", "getCreatedOfflineAt", "()J", "getDeviceId", "getFallDetected", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getLatitude", "()D", "getLocalId", "getLongitude", "getMessage", "getMovement", "getRiskLevel", "getServerEmergencyId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getSyncStatus", "getTriggeredBy", "getUserResponse", "component1", "component10", "component11", "component12", "component13", "component14", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;DDLjava/lang/Boolean;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;J)Lcom/emergency/aria/db/OfflineEmergency;", "equals", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "offline_emergencies")
public final class OfflineEmergency {
    @androidx.room.PrimaryKey
    @org.jetbrains.annotations.NotNull
    private final java.lang.String localId = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String message = null;
    private final double latitude = 0.0;
    private final double longitude = 0.0;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Boolean fallDetected = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String movement = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String userResponse = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String deviceId = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String capturedImagePath = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String triggeredBy = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String riskLevel = null;
    @androidx.room.ColumnInfo(name = "syncStatus")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String syncStatus = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Long serverEmergencyId = null;
    @androidx.room.ColumnInfo(name = "createdOfflineAt")
    private final long createdOfflineAt = 0L;
    
    public OfflineEmergency(@org.jetbrains.annotations.NotNull
    java.lang.String localId, @org.jetbrains.annotations.Nullable
    java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.Nullable
    java.lang.Boolean fallDetected, @org.jetbrains.annotations.Nullable
    java.lang.String movement, @org.jetbrains.annotations.Nullable
    java.lang.String userResponse, @org.jetbrains.annotations.NotNull
    java.lang.String deviceId, @org.jetbrains.annotations.Nullable
    java.lang.String capturedImagePath, @org.jetbrains.annotations.Nullable
    java.lang.String triggeredBy, @org.jetbrains.annotations.Nullable
    java.lang.String riskLevel, @org.jetbrains.annotations.NotNull
    java.lang.String syncStatus, @org.jetbrains.annotations.Nullable
    java.lang.Long serverEmergencyId, long createdOfflineAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getLocalId() {
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
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Boolean getFallDetected() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getMovement() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getUserResponse() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDeviceId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getCapturedImagePath() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getTriggeredBy() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getRiskLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSyncStatus() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getServerEmergencyId() {
        return null;
    }
    
    public final long getCreatedOfflineAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component12() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component13() {
        return null;
    }
    
    public final long component14() {
        return 0L;
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
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Boolean component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.emergency.aria.db.OfflineEmergency copy(@org.jetbrains.annotations.NotNull
    java.lang.String localId, @org.jetbrains.annotations.Nullable
    java.lang.String message, double latitude, double longitude, @org.jetbrains.annotations.Nullable
    java.lang.Boolean fallDetected, @org.jetbrains.annotations.Nullable
    java.lang.String movement, @org.jetbrains.annotations.Nullable
    java.lang.String userResponse, @org.jetbrains.annotations.NotNull
    java.lang.String deviceId, @org.jetbrains.annotations.Nullable
    java.lang.String capturedImagePath, @org.jetbrains.annotations.Nullable
    java.lang.String triggeredBy, @org.jetbrains.annotations.Nullable
    java.lang.String riskLevel, @org.jetbrains.annotations.NotNull
    java.lang.String syncStatus, @org.jetbrains.annotations.Nullable
    java.lang.Long serverEmergencyId, long createdOfflineAt) {
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