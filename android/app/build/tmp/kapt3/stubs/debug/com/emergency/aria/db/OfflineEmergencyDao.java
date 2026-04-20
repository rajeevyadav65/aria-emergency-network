package com.emergency.aria.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J\u0019\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0004H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\tJ\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u000bH\'J\u0011\u0010\f\u001a\u00020\rH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0005J!\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0014"}, d2 = {"Lcom/emergency/aria/db/OfflineEmergencyDao;", "", "getPendingSync", "", "Lcom/emergency/aria/db/OfflineEmergency;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "", "emergency", "(Lcom/emergency/aria/db/OfflineEmergency;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "pendingCount", "", "updateSyncStatus", "", "localId", "", "status", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao
public abstract interface OfflineEmergencyDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull
    com.emergency.aria.db.OfflineEmergency emergency, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM offline_emergencies WHERE syncStatus = \'PENDING\' ORDER BY createdOfflineAt ASC")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPendingSync(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.aria.db.OfflineEmergency>> $completion);
    
    @androidx.room.Query(value = "UPDATE offline_emergencies SET syncStatus = :status WHERE localId = :localId")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object updateSyncStatus(@org.jetbrains.annotations.NotNull
    java.lang.String localId, @org.jetbrains.annotations.NotNull
    java.lang.String status, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM offline_emergencies ORDER BY createdOfflineAt DESC")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.aria.db.OfflineEmergency>> observeAll();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM offline_emergencies WHERE syncStatus = \'PENDING\'")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object pendingCount(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}