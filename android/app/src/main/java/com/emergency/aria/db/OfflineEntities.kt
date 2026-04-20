package com.emergency.aria.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ── Entities (Tables) ─────────────────────────────────────────────────────────

@Entity(tableName = "offline_emergencies")
data class OfflineEmergency(
    @PrimaryKey val localId: String,
    val message: String?,
    val latitude: Double,
    val longitude: Double,
    val fallDetected: Boolean?=null,
    val movement: String?=null,
    val userResponse: String?=null,
    val deviceId: String,
    val capturedImagePath: String?=null,
    val triggeredBy: String?=null,          // "USER", "FALL_DETECT", "FACE_DETECT", "VOICE"
    val riskLevel: String?=null,
    @ColumnInfo(name = "syncStatus") val syncStatus: String = "PENDING",
    val serverEmergencyId: Long? = null,
    @ColumnInfo(name = "createdOfflineAt") val createdOfflineAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_location_history")
data class OfflineLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val speed: Float?,
    @ColumnInfo(name = "syncStatus") val syncStatus: String = "PENDING",
    @ColumnInfo(name = "recordedAt") val recordedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "received_alerts")
data class ReceivedAlert(
    @PrimaryKey val alertId: String,  // server alertId or BLE UUID
    val emergencyId: Long?,
    val riskLevel: String,
    val message: String?,
    val emergencyLat: Double?,
    val emergencyLon: Double?,
    val distanceMeters: Double?,
    val channel: String,              // "WEBSOCKET", "BLE", "WIFI_DIRECT"
    @ColumnInfo(name = "readStatus") val readStatus: String = "UNREAD",
    @ColumnInfo(name = "receivedAt") val receivedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "disaster_alerts_cache")
data class CachedDisasterAlert(
    @PrimaryKey val serverId: Long,
    val type: String,
    val title: String,
    val description: String?,
    val severity: String,
    val epicenterLat: Double?,
    val epicenterLon: Double?,
    val radiusKm: Double?,
    @ColumnInfo(name = "cachedAt") val cachedAt: Long = System.currentTimeMillis()
)

// ── DAOs (Database Access Objects) ────────────────────────────────────────────

@Dao
interface OfflineEmergencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(emergency: OfflineEmergency): Long

    // 🟢 FIX 1: getPending() ko getPendingSync() kar diya hai
    @Query("SELECT * FROM offline_emergencies WHERE syncStatus = 'PENDING' ORDER BY createdOfflineAt ASC")
    suspend fun getPendingSync(): List<OfflineEmergency>

    // 🟢 FIX 2: updateSync() ko updateSyncStatus() kar diya hai taaki SyncManager se match ho
    @Query("UPDATE offline_emergencies SET syncStatus = :status WHERE localId = :localId")
    suspend fun updateSyncStatus(localId: String, status: String)

    @Query("SELECT * FROM offline_emergencies ORDER BY createdOfflineAt DESC")
    fun observeAll(): Flow<List<OfflineEmergency>>

    @Query("SELECT COUNT(*) FROM offline_emergencies WHERE syncStatus = 'PENDING'")
    suspend fun pendingCount(): Int
}

@Dao
interface OfflineLocationDao {
    @Insert
    suspend fun insert(location: OfflineLocation): Long

    @Query("SELECT * FROM offline_location_history WHERE syncStatus = 'PENDING' ORDER BY recordedAt ASC LIMIT 100")
    suspend fun getPending(): List<OfflineLocation>

    @Query("UPDATE offline_location_history SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("DELETE FROM offline_location_history WHERE recordedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long): Int
}

@Dao
interface ReceivedAlertDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(alert: ReceivedAlert): Long

    @Query("SELECT * FROM received_alerts ORDER BY receivedAt DESC")
    fun observeAll(): Flow<List<ReceivedAlert>>

    @Query("SELECT COUNT(*) FROM received_alerts WHERE readStatus = 'UNREAD'")
    fun observeUnreadCount(): Flow<Int>

    @Query("UPDATE received_alerts SET readStatus = 'READ' WHERE alertId = :alertId")
    suspend fun markRead(alertId: String)

    @Query("DELETE FROM received_alerts WHERE receivedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long): Int
}

@Dao
interface CachedDisasterAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<CachedDisasterAlert>)

    @Query("SELECT * FROM disaster_alerts_cache ORDER BY cachedAt DESC")
    fun observeAll(): Flow<List<CachedDisasterAlert>>

    @Query("DELETE FROM disaster_alerts_cache WHERE cachedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long): Int
}