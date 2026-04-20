package com.emergency.aria.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
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
public final class ReceivedAlertDao_Impl implements ReceivedAlertDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ReceivedAlert> __insertionAdapterOfReceivedAlert;

  private final SharedSQLiteStatement __preparedStmtOfMarkRead;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public ReceivedAlertDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceivedAlert = new EntityInsertionAdapter<ReceivedAlert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `received_alerts` (`alertId`,`emergencyId`,`riskLevel`,`message`,`emergencyLat`,`emergencyLon`,`distanceMeters`,`channel`,`readStatus`,`receivedAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ReceivedAlert entity) {
        statement.bindString(1, entity.getAlertId());
        if (entity.getEmergencyId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getEmergencyId());
        }
        statement.bindString(3, entity.getRiskLevel());
        if (entity.getMessage() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMessage());
        }
        if (entity.getEmergencyLat() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getEmergencyLat());
        }
        if (entity.getEmergencyLon() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getEmergencyLon());
        }
        if (entity.getDistanceMeters() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getDistanceMeters());
        }
        statement.bindString(8, entity.getChannel());
        statement.bindString(9, entity.getReadStatus());
        statement.bindLong(10, entity.getReceivedAt());
      }
    };
    this.__preparedStmtOfMarkRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE received_alerts SET readStatus = 'READ' WHERE alertId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM received_alerts WHERE receivedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ReceivedAlert alert, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReceivedAlert.insertAndReturnId(alert);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markRead(final String alertId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkRead.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, alertId);
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
          __preparedStmtOfMarkRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long cutoff,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoff);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ReceivedAlert>> observeAll() {
    final String _sql = "SELECT * FROM received_alerts ORDER BY receivedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"received_alerts"}, new Callable<List<ReceivedAlert>>() {
      @Override
      @NonNull
      public List<ReceivedAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfAlertId = CursorUtil.getColumnIndexOrThrow(_cursor, "alertId");
          final int _cursorIndexOfEmergencyId = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyId");
          final int _cursorIndexOfRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "riskLevel");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfEmergencyLat = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyLat");
          final int _cursorIndexOfEmergencyLon = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyLon");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfChannel = CursorUtil.getColumnIndexOrThrow(_cursor, "channel");
          final int _cursorIndexOfReadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "readStatus");
          final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "receivedAt");
          final List<ReceivedAlert> _result = new ArrayList<ReceivedAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ReceivedAlert _item;
            final String _tmpAlertId;
            _tmpAlertId = _cursor.getString(_cursorIndexOfAlertId);
            final Long _tmpEmergencyId;
            if (_cursor.isNull(_cursorIndexOfEmergencyId)) {
              _tmpEmergencyId = null;
            } else {
              _tmpEmergencyId = _cursor.getLong(_cursorIndexOfEmergencyId);
            }
            final String _tmpRiskLevel;
            _tmpRiskLevel = _cursor.getString(_cursorIndexOfRiskLevel);
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            final Double _tmpEmergencyLat;
            if (_cursor.isNull(_cursorIndexOfEmergencyLat)) {
              _tmpEmergencyLat = null;
            } else {
              _tmpEmergencyLat = _cursor.getDouble(_cursorIndexOfEmergencyLat);
            }
            final Double _tmpEmergencyLon;
            if (_cursor.isNull(_cursorIndexOfEmergencyLon)) {
              _tmpEmergencyLon = null;
            } else {
              _tmpEmergencyLon = _cursor.getDouble(_cursorIndexOfEmergencyLon);
            }
            final Double _tmpDistanceMeters;
            if (_cursor.isNull(_cursorIndexOfDistanceMeters)) {
              _tmpDistanceMeters = null;
            } else {
              _tmpDistanceMeters = _cursor.getDouble(_cursorIndexOfDistanceMeters);
            }
            final String _tmpChannel;
            _tmpChannel = _cursor.getString(_cursorIndexOfChannel);
            final String _tmpReadStatus;
            _tmpReadStatus = _cursor.getString(_cursorIndexOfReadStatus);
            final long _tmpReceivedAt;
            _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
            _item = new ReceivedAlert(_tmpAlertId,_tmpEmergencyId,_tmpRiskLevel,_tmpMessage,_tmpEmergencyLat,_tmpEmergencyLon,_tmpDistanceMeters,_tmpChannel,_tmpReadStatus,_tmpReceivedAt);
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
  public Flow<Integer> observeUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM received_alerts WHERE readStatus = 'UNREAD'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"received_alerts"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
