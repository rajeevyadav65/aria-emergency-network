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
public final class CachedDisasterAlertDao_Impl implements CachedDisasterAlertDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CachedDisasterAlert> __insertionAdapterOfCachedDisasterAlert;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public CachedDisasterAlertDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCachedDisasterAlert = new EntityInsertionAdapter<CachedDisasterAlert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `disaster_alerts_cache` (`serverId`,`type`,`title`,`description`,`severity`,`epicenterLat`,`epicenterLon`,`radiusKm`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CachedDisasterAlert entity) {
        statement.bindLong(1, entity.getServerId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getTitle());
        if (entity.getDescription() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDescription());
        }
        statement.bindString(5, entity.getSeverity());
        if (entity.getEpicenterLat() == null) {
          statement.bindNull(6);
        } else {
          statement.bindDouble(6, entity.getEpicenterLat());
        }
        if (entity.getEpicenterLon() == null) {
          statement.bindNull(7);
        } else {
          statement.bindDouble(7, entity.getEpicenterLon());
        }
        if (entity.getRadiusKm() == null) {
          statement.bindNull(8);
        } else {
          statement.bindDouble(8, entity.getRadiusKm());
        }
        statement.bindLong(9, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM disaster_alerts_cache WHERE cachedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAll(final List<CachedDisasterAlert> alerts,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCachedDisasterAlert.insert(alerts);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Flow<List<CachedDisasterAlert>> observeAll() {
    final String _sql = "SELECT * FROM disaster_alerts_cache ORDER BY cachedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"disaster_alerts_cache"}, new Callable<List<CachedDisasterAlert>>() {
      @Override
      @NonNull
      public List<CachedDisasterAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfServerId = CursorUtil.getColumnIndexOrThrow(_cursor, "serverId");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfEpicenterLat = CursorUtil.getColumnIndexOrThrow(_cursor, "epicenterLat");
          final int _cursorIndexOfEpicenterLon = CursorUtil.getColumnIndexOrThrow(_cursor, "epicenterLon");
          final int _cursorIndexOfRadiusKm = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusKm");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<CachedDisasterAlert> _result = new ArrayList<CachedDisasterAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CachedDisasterAlert _item;
            final long _tmpServerId;
            _tmpServerId = _cursor.getLong(_cursorIndexOfServerId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpSeverity;
            _tmpSeverity = _cursor.getString(_cursorIndexOfSeverity);
            final Double _tmpEpicenterLat;
            if (_cursor.isNull(_cursorIndexOfEpicenterLat)) {
              _tmpEpicenterLat = null;
            } else {
              _tmpEpicenterLat = _cursor.getDouble(_cursorIndexOfEpicenterLat);
            }
            final Double _tmpEpicenterLon;
            if (_cursor.isNull(_cursorIndexOfEpicenterLon)) {
              _tmpEpicenterLon = null;
            } else {
              _tmpEpicenterLon = _cursor.getDouble(_cursorIndexOfEpicenterLon);
            }
            final Double _tmpRadiusKm;
            if (_cursor.isNull(_cursorIndexOfRadiusKm)) {
              _tmpRadiusKm = null;
            } else {
              _tmpRadiusKm = _cursor.getDouble(_cursorIndexOfRadiusKm);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new CachedDisasterAlert(_tmpServerId,_tmpType,_tmpTitle,_tmpDescription,_tmpSeverity,_tmpEpicenterLat,_tmpEpicenterLon,_tmpRadiusKm,_tmpCachedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
