package com.emergency.aria.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OfflineEmergencyDao_Impl implements OfflineEmergencyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OfflineEmergency> __insertionAdapterOfOfflineEmergency;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSyncStatus;

  public OfflineEmergencyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOfflineEmergency = new EntityInsertionAdapter<OfflineEmergency>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `offline_emergencies` (`localId`,`message`,`latitude`,`longitude`,`fallDetected`,`movement`,`userResponse`,`deviceId`,`capturedImagePath`,`triggeredBy`,`riskLevel`,`syncStatus`,`serverEmergencyId`,`createdOfflineAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OfflineEmergency entity) {
        statement.bindString(1, entity.getLocalId());
        if (entity.getMessage() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getMessage());
        }
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        final Integer _tmp = entity.getFallDetected() == null ? null : (entity.getFallDetected() ? 1 : 0);
        if (_tmp == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, _tmp);
        }
        if (entity.getMovement() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMovement());
        }
        if (entity.getUserResponse() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getUserResponse());
        }
        statement.bindString(8, entity.getDeviceId());
        if (entity.getCapturedImagePath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getCapturedImagePath());
        }
        if (entity.getTriggeredBy() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getTriggeredBy());
        }
        if (entity.getRiskLevel() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getRiskLevel());
        }
        statement.bindString(12, entity.getSyncStatus());
        if (entity.getServerEmergencyId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getServerEmergencyId());
        }
        statement.bindLong(14, entity.getCreatedOfflineAt());
      }
    };
    this.__preparedStmtOfUpdateSyncStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE offline_emergencies SET syncStatus = ? WHERE localId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final OfflineEmergency emergency,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfOfflineEmergency.insertAndReturnId(emergency);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSyncStatus(final String localId, final String status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSyncStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, localId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateSyncStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingSync(final Continuation<? super List<OfflineEmergency>> $completion) {
    final String _sql = "SELECT * FROM offline_emergencies WHERE syncStatus = 'PENDING' ORDER BY createdOfflineAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OfflineEmergency>>() {
      @Override
      @NonNull
      public List<OfflineEmergency> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfFallDetected = CursorUtil.getColumnIndexOrThrow(_cursor, "fallDetected");
          final int _cursorIndexOfMovement = CursorUtil.getColumnIndexOrThrow(_cursor, "movement");
          final int _cursorIndexOfUserResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "userResponse");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfCapturedImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedImagePath");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfServerEmergencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverEmergencyId");
          final int _cursorIndexOfCreatedOfflineAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdOfflineAt");
          final List<OfflineEmergency> _result = new ArrayList<OfflineEmergency>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OfflineEmergency _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Boolean _tmpFallDetected;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfFallDetected)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfFallDetected);
            }
            _tmpFallDetected = _tmp == null ? null : _tmp != 0;
            final String _tmpMovement;
            if (_cursor.isNull(_cursorIndexOfMovement)) {
              _tmpMovement = null;
            } else {
              _tmpMovement = _cursor.getString(_cursorIndexOfMovement);
            }
            final String _tmpUserResponse;
            if (_cursor.isNull(_cursorIndexOfUserResponse)) {
              _tmpUserResponse = null;
            } else {
              _tmpUserResponse = _cursor.getString(_cursorIndexOfUserResponse);
            }
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpCapturedImagePath;
            if (_cursor.isNull(_cursorIndexOfCapturedImagePath)) {
              _tmpCapturedImagePath = null;
            } else {
              _tmpCapturedImagePath = _cursor.getString(_cursorIndexOfCapturedImagePath);
            }
            final String _tmpTriggeredBy;
            if (_cursor.isNull(_cursorIndexOfTriggeredBy)) {
              _tmpTriggeredBy = null;
            } else {
              _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            }
            final String _tmpRiskLevel;
            if (_cursor.isNull(_cursorIndexOfRiskLevel)) {
              _tmpRiskLevel = null;
            } else {
              _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final Long _tmpServerEmergencyId;
            if (_cursor.isNull(_cursorIndexOfServerEmergencyId)) {
              _tmpServerEmergencyId = null;
            } else {
              _tmpServerEmergencyId = _cursor.getLong(_cursorIndexOfServerEmergencyId);
            }
            final long _tmpCreatedOfflineAt;
            _tmpCreatedOfflineAt = _cursor.getLong(_cursorIndexOfCreatedOfflineAt);
            _item = new OfflineEmergency(_tmpLocalId,_tmpMessage,_tmpLatitude,_tmpLongitude,_tmpFallDetected,_tmpMovement,_tmpUserResponse,_tmpDeviceId,_tmpCapturedImagePath,_tmpTriggeredBy,_tmpRiskLevel,_tmpSyncStatus,_tmpServerEmergencyId,_tmpCreatedOfflineAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<OfflineEmergency>> observeAll() {
    final String _sql = "SELECT * FROM offline_emergencies ORDER BY createdOfflineAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"offline_emergencies"}, new Callable<List<OfflineEmergency>>() {
      @Override
      @NonNull
      public List<OfflineEmergency> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfFallDetected = CursorUtil.getColumnIndexOrThrow(_cursor, "fallDetected");
          final int _cursorIndexOfMovement = CursorUtil.getColumnIndexOrThrow(_cursor, "movement");
          final int _cursorIndexOfUserResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "userResponse");
          final int _cursorIndexOfDeviceId = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceId");
          final int _cursorIndexOfCapturedImagePath = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedImagePath");
          final int _cursorIndexOfTriggeredBy = CursorUtil.getColumnIndexOrThrow(_cursor, "triggeredBy");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfSyncStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "syncStatus");
          final int _cursorIndexOfServerEmergencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverEmergencyId");
          final int _cursorIndexOfCreatedOfflineAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdOfflineAt");
          final List<OfflineEmergency> _result = new ArrayList<OfflineEmergency>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OfflineEmergency _item;
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Boolean _tmpFallDetected;
            final Integer _tmp;
            if (_cursor.isNull(_cursorIndexOfFallDetected)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(_cursorIndexOfFallDetected);
            }
            _tmpFallDetected = _tmp == null ? null : _tmp != 0;
            final String _tmpMovement;
            if (_cursor.isNull(_cursorIndexOfMovement)) {
              _tmpMovement = null;
            } else {
              _tmpMovement = _cursor.getString(_cursorIndexOfMovement);
            }
            final String _tmpUserResponse;
            if (_cursor.isNull(_cursorIndexOfUserResponse)) {
              _tmpUserResponse = null;
            } else {
              _tmpUserResponse = _cursor.getString(_cursorIndexOfUserResponse);
            }
            final String _tmpDeviceId;
            _tmpDeviceId = _cursor.getString(_cursorIndexOfDeviceId);
            final String _tmpCapturedImagePath;
            if (_cursor.isNull(_cursorIndexOfCapturedImagePath)) {
              _tmpCapturedImagePath = null;
            } else {
              _tmpCapturedImagePath = _cursor.getString(_cursorIndexOfCapturedImagePath);
            }
            final String _tmpTriggeredBy;
            if (_cursor.isNull(_cursorIndexOfTriggeredBy)) {
              _tmpTriggeredBy = null;
            } else {
              _tmpTriggeredBy = _cursor.getString(_cursorIndexOfTriggeredBy);
            }
            final String _tmpRiskLevel;
            if (_cursor.isNull(_cursorIndexOfRiskLevel)) {
              _tmpRiskLevel = null;
            } else {
              _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            }
            final String _tmpSyncStatus;
            _tmpSyncStatus = _cursor.getString(_cursorIndexOfSyncStatus);
            final Long _tmpServerEmergencyId;
            if (_cursor.isNull(_cursorIndexOfServerEmergencyId)) {
              _tmpServerEmergencyId = null;
            } else {
              _tmpServerEmergencyId = _cursor.getLong(_cursorIndexOfServerEmergencyId);
            }
            final long _tmpCreatedOfflineAt;
            _tmpCreatedOfflineAt = _cursor.getLong(_cursorIndexOfCreatedOfflineAt);
            _item = new OfflineEmergency(_tmpLocalId,_tmpMessage,_tmpLatitude,_tmpLongitude,_tmpFallDetected,_tmpMovement,_tmpUserResponse,_tmpDeviceId,_tmpCapturedImagePath,_tmpTriggeredBy,_tmpRiskLevel,_tmpSyncStatus,_tmpServerEmergencyId,_tmpCreatedOfflineAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object pendingCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM offline_emergencies WHERE syncStatus = 'PENDING'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
