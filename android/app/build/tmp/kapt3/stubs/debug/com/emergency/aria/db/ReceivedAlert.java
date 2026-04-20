package com.emergency.aria.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b#\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u000fJ\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003J\u0010\u0010\"\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010%\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010&\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010\'\u001a\u0004\u0018\u00010\tH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J|\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u0005H\u00c6\u0001\u00a2\u0006\u0002\u0010+J\u0013\u0010,\u001a\u00020-2\b\u0010.\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010/\u001a\u000200H\u00d6\u0001J\t\u00101\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0015\u0010\u000b\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\u0015\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0019\u0010\u0014R\u0015\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u001a\u0010\u0014R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011R\u0016\u0010\r\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0011R\u0016\u0010\u000e\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0011\u00a8\u00062"}, d2 = {"Lcom/emergency/aria/db/ReceivedAlert;", "", "alertId", "", "emergencyId", "", "riskLevel", "message", "emergencyLat", "", "emergencyLon", "distanceMeters", "channel", "readStatus", "receivedAt", "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Double;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;J)V", "getAlertId", "()Ljava/lang/String;", "getChannel", "getDistanceMeters", "()Ljava/lang/Double;", "Ljava/lang/Double;", "getEmergencyId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getEmergencyLat", "getEmergencyLon", "getMessage", "getReadStatus", "getReceivedAt", "()J", "getRiskLevel", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Double;Ljava/lang/Double;Ljava/lang/Double;Ljava/lang/String;Ljava/lang/String;J)Lcom/emergency/aria/db/ReceivedAlert;", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
@androidx.room.Entity(tableName = "received_alerts")
public final class ReceivedAlert {
    @androidx.room.PrimaryKey
    @org.jetbrains.annotations.NotNull
    private final java.lang.String alertId = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Long emergencyId = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String riskLevel = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String message = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Double emergencyLat = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Double emergencyLon = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Double distanceMeters = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String channel = null;
    @androidx.room.ColumnInfo(name = "readStatus")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String readStatus = null;
    @androidx.room.ColumnInfo(name = "receivedAt")
    private final long receivedAt = 0L;
    
    public ReceivedAlert(@org.jetbrains.annotations.NotNull
    java.lang.String alertId, @org.jetbrains.annotations.Nullable
    java.lang.Long emergencyId, @org.jetbrains.annotations.NotNull
    java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
    java.lang.String message, @org.jetbrains.annotations.Nullable
    java.lang.Double emergencyLat, @org.jetbrains.annotations.Nullable
    java.lang.Double emergencyLon, @org.jetbrains.annotations.Nullable
    java.lang.Double distanceMeters, @org.jetbrains.annotations.NotNull
    java.lang.String channel, @org.jetbrains.annotations.NotNull
    java.lang.String readStatus, long receivedAt) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getAlertId() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getEmergencyId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getRiskLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double getEmergencyLat() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double getEmergencyLon() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double getDistanceMeters() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getChannel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getReadStatus() {
        return null;
    }
    
    public final long getReceivedAt() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    public final long component10() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double component6() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Double component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.emergency.aria.db.ReceivedAlert copy(@org.jetbrains.annotations.NotNull
    java.lang.String alertId, @org.jetbrains.annotations.Nullable
    java.lang.Long emergencyId, @org.jetbrains.annotations.NotNull
    java.lang.String riskLevel, @org.jetbrains.annotations.Nullable
    java.lang.String message, @org.jetbrains.annotations.Nullable
    java.lang.Double emergencyLat, @org.jetbrains.annotations.Nullable
    java.lang.Double emergencyLon, @org.jetbrains.annotations.Nullable
    java.lang.Double distanceMeters, @org.jetbrains.annotations.NotNull
    java.lang.String channel, @org.jetbrains.annotations.NotNull
    java.lang.String readStatus, long receivedAt) {
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