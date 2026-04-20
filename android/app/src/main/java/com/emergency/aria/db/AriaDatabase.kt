package com.emergency.aria.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context



@Database(
    entities = [
        OfflineEmergency::class,
        OfflineLocation::class,
        ReceivedAlert::class,
        CachedDisasterAlert::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AriaDatabase : RoomDatabase() {
    abstract fun emergencyDao(): OfflineEmergencyDao
    abstract fun locationDao(): OfflineLocationDao
    abstract fun alertDao(): ReceivedAlertDao
    abstract fun disasterDao(): CachedDisasterAlertDao

    companion object {
        @Volatile private var INSTANCE: AriaDatabase? = null

        fun getInstance(context: Context): AriaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AriaDatabase::class.java,
                    "aria_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}