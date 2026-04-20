package com.emergency.aria.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0006J\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0019\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\rJ\u001f\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0011\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0012"}, d2 = {"Lcom/emergency/aria/db/OfflineLocationDao;", "", "deleteOlderThan", "", "cutoff", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPending", "", "Lcom/emergency/aria/db/OfflineLocation;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "location", "(Lcom/emergency/aria/db/OfflineLocation;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markSynced", "", "ids", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao
public abstract interface OfflineLocationDao {
    
    @androidx.room.Insert
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull
    com.emergency.aria.db.OfflineLocation location, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM offline_location_history WHERE syncStatus = \'PENDING\' ORDER BY recordedAt ASC LIMIT 100")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPending(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.aria.db.OfflineLocation>> $completion);
    
    @androidx.room.Query(value = "UPDATE offline_location_history SET syncStatus = \'SYNCED\' WHERE id IN (:ids)")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markSynced(@org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Long> ids, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM offline_location_history WHERE recordedAt < :cutoff")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object deleteOlderThan(long cutoff, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}