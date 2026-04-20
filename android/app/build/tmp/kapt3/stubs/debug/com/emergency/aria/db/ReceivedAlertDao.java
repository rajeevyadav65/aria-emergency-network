package com.emergency.aria.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0019\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0019\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00120\u0011H\'J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u0011H\'\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0014"}, d2 = {"Lcom/emergency/aria/db/ReceivedAlertDao;", "", "deleteOlderThan", "", "cutoff", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "alert", "Lcom/emergency/aria/db/ReceivedAlert;", "(Lcom/emergency/aria/db/ReceivedAlert;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markRead", "", "alertId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeAll", "Lkotlinx/coroutines/flow/Flow;", "", "observeUnreadCount", "app_debug"})
@androidx.room.Dao
public abstract interface ReceivedAlertDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull
    com.emergency.aria.db.ReceivedAlert alert, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM received_alerts ORDER BY receivedAt DESC")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.aria.db.ReceivedAlert>> observeAll();
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM received_alerts WHERE readStatus = \'UNREAD\'")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> observeUnreadCount();
    
    @androidx.room.Query(value = "UPDATE received_alerts SET readStatus = \'READ\' WHERE alertId = :alertId")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markRead(@org.jetbrains.annotations.NotNull
    java.lang.String alertId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM received_alerts WHERE receivedAt < :cutoff")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object deleteOlderThan(long cutoff, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}