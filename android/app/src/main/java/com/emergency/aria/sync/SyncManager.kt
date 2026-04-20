package com.emergency.aria.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.emergency.aria.db.AriaDatabase
import com.emergency.aria.db.OfflineEmergency as SyncQueueItem
import com.emergency.aria.service.EmergencyPayload
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AriaDatabase,
    private val retrofit: Retrofit
) {
    private val gson = Gson()

    // 🟢 Ensure kijiye ki AriaDatabase mein method ka naam offlineEmergencyDao() hi ho
    private val syncQueueDao = db.emergencyDao()

    suspend fun enqueue(payload: EmergencyPayload) {
        syncQueueDao.insert(SyncQueueItem(
            localId = payload.localId,
            message = payload.message ?: "Emergency",
            latitude = payload.latitude,
            longitude = payload.longitude,
            deviceId = payload.deviceId,
            riskLevel = payload.riskLevel,
            syncStatus = "PENDING" // 🟢 Extra comma removed
        ))
        scheduleBackgroundSync()
        Log.i("SyncManager", "Enqueued emergency ${payload.localId} for sync")
    }

    suspend fun syncPendingQueue() = withContext(Dispatchers.IO) {
        // 🟢 Explicit List type taki 'forEach' error na de
        val pending: List<SyncQueueItem> = syncQueueDao.getPendingSync()

        if (pending.isEmpty()) return@withContext

        try {
            val api = retrofit.create(SyncApiService::class.java)
            val response = api.syncBatch(mapOf("items" to pending))

            if (response.isSuccessful) {
                pending.forEach { item -> // 🟢 'it' ki jagah explicitly 'item' use kiya
                    syncQueueDao.updateSyncStatus(item.localId, "SYNCED")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "Sync failed: ${e.message}")
        }
    }

    private fun scheduleBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork("aria_sync", ExistingWorkPolicy.KEEP, request)
    }
}

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        syncManager.syncPendingQueue()
        return Result.success()
    }
}

interface SyncApiService {
    @POST("api/sync/batch")
    // 🟢 @JvmSuppressWildcards lagana zaroori hai Kotlin mein
    suspend fun syncBatch(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<Map<String, Any>>
}