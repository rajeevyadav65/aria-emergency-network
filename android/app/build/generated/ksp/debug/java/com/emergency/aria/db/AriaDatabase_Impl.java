package com.emergency.aria.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AriaDatabase_Impl extends AriaDatabase {
  private volatile OfflineEmergencyDao _offlineEmergencyDao;

  private volatile OfflineLocationDao _offlineLocationDao;

  private volatile ReceivedAlertDao _receivedAlertDao;

  private volatile CachedDisasterAlertDao _cachedDisasterAlertDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `offline_emergencies` (`localId` TEXT NOT NULL, `message` TEXT, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `fallDetected` INTEGER, `movement` TEXT, `userResponse` TEXT, `deviceId` TEXT NOT NULL, `capturedImagePath` TEXT, `triggeredBy` TEXT, `riskLevel` TEXT, `syncStatus` TEXT NOT NULL, `serverEmergencyId` INTEGER, `createdOfflineAt` INTEGER NOT NULL, PRIMARY KEY(`localId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `offline_location_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `deviceId` TEXT NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `accuracy` REAL, `speed` REAL, `syncStatus` TEXT NOT NULL, `recordedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `received_alerts` (`alertId` TEXT NOT NULL, `emergencyId` INTEGER, `riskLevel` TEXT NOT NULL, `message` TEXT, `emergencyLat` REAL, `emergencyLon` REAL, `distanceMeters` REAL, `channel` TEXT NOT NULL, `readStatus` TEXT NOT NULL, `receivedAt` INTEGER NOT NULL, PRIMARY KEY(`alertId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `disaster_alerts_cache` (`serverId` INTEGER NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `severity` TEXT NOT NULL, `epicenterLat` REAL, `epicenterLon` REAL, `radiusKm` REAL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`serverId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '50204134343870b1c313b009c7738f5c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `offline_emergencies`");
        db.execSQL("DROP TABLE IF EXISTS `offline_location_history`");
        db.execSQL("DROP TABLE IF EXISTS `received_alerts`");
        db.execSQL("DROP TABLE IF EXISTS `disaster_alerts_cache`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsOfflineEmergencies = new HashMap<String, TableInfo.Column>(14);
        _columnsOfflineEmergencies.put("localId", new TableInfo.Column("localId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("fallDetected", new TableInfo.Column("fallDetected", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("movement", new TableInfo.Column("movement", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("userResponse", new TableInfo.Column("userResponse", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("capturedImagePath", new TableInfo.Column("capturedImagePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("triggeredBy", new TableInfo.Column("triggeredBy", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("riskLevel", new TableInfo.Column("riskLevel", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("serverEmergencyId", new TableInfo.Column("serverEmergencyId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineEmergencies.put("createdOfflineAt", new TableInfo.Column("createdOfflineAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflineEmergencies = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflineEmergencies = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOfflineEmergencies = new TableInfo("offline_emergencies", _columnsOfflineEmergencies, _foreignKeysOfflineEmergencies, _indicesOfflineEmergencies);
        final TableInfo _existingOfflineEmergencies = TableInfo.read(db, "offline_emergencies");
        if (!_infoOfflineEmergencies.equals(_existingOfflineEmergencies)) {
          return new RoomOpenHelper.ValidationResult(false, "offline_emergencies(com.emergency.aria.db.OfflineEmergency).\n"
                  + " Expected:\n" + _infoOfflineEmergencies + "\n"
                  + " Found:\n" + _existingOfflineEmergencies);
        }
        final HashMap<String, TableInfo.Column> _columnsOfflineLocationHistory = new HashMap<String, TableInfo.Column>(8);
        _columnsOfflineLocationHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("deviceId", new TableInfo.Column("deviceId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("accuracy", new TableInfo.Column("accuracy", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("speed", new TableInfo.Column("speed", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("syncStatus", new TableInfo.Column("syncStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOfflineLocationHistory.put("recordedAt", new TableInfo.Column("recordedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOfflineLocationHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOfflineLocationHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOfflineLocationHistory = new TableInfo("offline_location_history", _columnsOfflineLocationHistory, _foreignKeysOfflineLocationHistory, _indicesOfflineLocationHistory);
        final TableInfo _existingOfflineLocationHistory = TableInfo.read(db, "offline_location_history");
        if (!_infoOfflineLocationHistory.equals(_existingOfflineLocationHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "offline_location_history(com.emergency.aria.db.OfflineLocation).\n"
                  + " Expected:\n" + _infoOfflineLocationHistory + "\n"
                  + " Found:\n" + _existingOfflineLocationHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsReceivedAlerts = new HashMap<String, TableInfo.Column>(10);
        _columnsReceivedAlerts.put("alertId", new TableInfo.Column("alertId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("emergencyId", new TableInfo.Column("emergencyId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("riskLevel", new TableInfo.Column("riskLevel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("emergencyLat", new TableInfo.Column("emergencyLat", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("emergencyLon", new TableInfo.Column("emergencyLon", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("distanceMeters", new TableInfo.Column("distanceMeters", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("channel", new TableInfo.Column("channel", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("readStatus", new TableInfo.Column("readStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceivedAlerts.put("receivedAt", new TableInfo.Column("receivedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceivedAlerts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceivedAlerts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceivedAlerts = new TableInfo("received_alerts", _columnsReceivedAlerts, _foreignKeysReceivedAlerts, _indicesReceivedAlerts);
        final TableInfo _existingReceivedAlerts = TableInfo.read(db, "received_alerts");
        if (!_infoReceivedAlerts.equals(_existingReceivedAlerts)) {
          return new RoomOpenHelper.ValidationResult(false, "received_alerts(com.emergency.aria.db.ReceivedAlert).\n"
                  + " Expected:\n" + _infoReceivedAlerts + "\n"
                  + " Found:\n" + _existingReceivedAlerts);
        }
        final HashMap<String, TableInfo.Column> _columnsDisasterAlertsCache = new HashMap<String, TableInfo.Column>(9);
        _columnsDisasterAlertsCache.put("serverId", new TableInfo.Column("serverId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("epicenterLat", new TableInfo.Column("epicenterLat", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("epicenterLon", new TableInfo.Column("epicenterLon", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("radiusKm", new TableInfo.Column("radiusKm", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDisasterAlertsCache.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDisasterAlertsCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDisasterAlertsCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDisasterAlertsCache = new TableInfo("disaster_alerts_cache", _columnsDisasterAlertsCache, _foreignKeysDisasterAlertsCache, _indicesDisasterAlertsCache);
        final TableInfo _existingDisasterAlertsCache = TableInfo.read(db, "disaster_alerts_cache");
        if (!_infoDisasterAlertsCache.equals(_existingDisasterAlertsCache)) {
          return new RoomOpenHelper.ValidationResult(false, "disaster_alerts_cache(com.emergency.aria.db.CachedDisasterAlert).\n"
                  + " Expected:\n" + _infoDisasterAlertsCache + "\n"
                  + " Found:\n" + _existingDisasterAlertsCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "50204134343870b1c313b009c7738f5c", "1e23c5ec512cb8e0f14a68870af228a5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "offline_emergencies","offline_location_history","received_alerts","disaster_alerts_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `offline_emergencies`");
      _db.execSQL("DELETE FROM `offline_location_history`");
      _db.execSQL("DELETE FROM `received_alerts`");
      _db.execSQL("DELETE FROM `disaster_alerts_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(OfflineEmergencyDao.class, OfflineEmergencyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OfflineLocationDao.class, OfflineLocationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ReceivedAlertDao.class, ReceivedAlertDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CachedDisasterAlertDao.class, CachedDisasterAlertDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public OfflineEmergencyDao emergencyDao() {
    if (_offlineEmergencyDao != null) {
      return _offlineEmergencyDao;
    } else {
      synchronized(this) {
        if(_offlineEmergencyDao == null) {
          _offlineEmergencyDao = new OfflineEmergencyDao_Impl(this);
        }
        return _offlineEmergencyDao;
      }
    }
  }

  @Override
  public OfflineLocationDao locationDao() {
    if (_offlineLocationDao != null) {
      return _offlineLocationDao;
    } else {
      synchronized(this) {
        if(_offlineLocationDao == null) {
          _offlineLocationDao = new OfflineLocationDao_Impl(this);
        }
        return _offlineLocationDao;
      }
    }
  }

  @Override
  public ReceivedAlertDao alertDao() {
    if (_receivedAlertDao != null) {
      return _receivedAlertDao;
    } else {
      synchronized(this) {
        if(_receivedAlertDao == null) {
          _receivedAlertDao = new ReceivedAlertDao_Impl(this);
        }
        return _receivedAlertDao;
      }
    }
  }

  @Override
  public CachedDisasterAlertDao disasterDao() {
    if (_cachedDisasterAlertDao != null) {
      return _cachedDisasterAlertDao;
    } else {
      synchronized(this) {
        if(_cachedDisasterAlertDao == null) {
          _cachedDisasterAlertDao = new CachedDisasterAlertDao_Impl(this);
        }
        return _cachedDisasterAlertDao;
      }
    }
  }
}
