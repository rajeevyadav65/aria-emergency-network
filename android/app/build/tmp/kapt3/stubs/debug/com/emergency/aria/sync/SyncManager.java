package com.emergency.aria.sync;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B!\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0019\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0011J\b\u0010\u0012\u001a\u00020\u000eH\u0002J\u0011\u0010\u0013\u001a\u00020\u000eH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0015"}, d2 = {"Lcom/emergency/aria/sync/SyncManager;", "", "context", "Landroid/content/Context;", "db", "Lcom/emergency/aria/db/AriaDatabase;", "retrofit", "Lretrofit2/Retrofit;", "(Landroid/content/Context;Lcom/emergency/aria/db/AriaDatabase;Lretrofit2/Retrofit;)V", "gson", "Lcom/google/gson/Gson;", "syncQueueDao", "Lcom/emergency/aria/db/OfflineEmergencyDao;", "enqueue", "", "payload", "Lcom/emergency/aria/service/EmergencyPayload;", "(Lcom/emergency/aria/service/EmergencyPayload;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scheduleBackgroundSync", "syncPendingQueue", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SyncManager {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.db.AriaDatabase db = null;
    @org.jetbrains.annotations.NotNull
    private final retrofit2.Retrofit retrofit = null;
    @org.jetbrains.annotations.NotNull
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.aria.db.OfflineEmergencyDao syncQueueDao = null;
    
    @javax.inject.Inject
    public SyncManager(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.emergency.aria.db.AriaDatabase db, @org.jetbrains.annotations.NotNull
    retrofit2.Retrofit retrofit) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object enqueue(@org.jetbrains.annotations.NotNull
    com.emergency.aria.service.EmergencyPayload payload, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object syncPendingQueue(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void scheduleBackgroundSync() {
    }
}