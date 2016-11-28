package com.mju.hps.withme.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mju.hps.withme.database.DatabaseSchema.UserTable;
import com.mju.hps.withme.model.User;

/**
 * Created by KMC on 2016. 11. 29..
 */

public class DatabaseProvider extends ContentProvider {
    private static final String URI = "content://com.mju.hps.withme/DatabaseProvider";
    public static final Uri CONTENT_URI = Uri.parse(URI);
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.example.yutae.exam2_1", "MyDB", 1);
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        DatabaseLab.getInstance().getDatabase().delete(UserTable.NAME, selection, selectionArgs);
        return 0;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long rowID = DatabaseLab.getInstance().getDatabase().insert(UserTable.NAME, null, values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
//        Log.i("hhh", "create!!!!!!");
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        Log.e("Provider", "들어옴");
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(UserTable.NAME);
        String orderBy = UserTable.Cols.NAME;

        Cursor c = qb.query(DatabaseLab.getInstance().getDatabase(), projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        Log.i("Match!", "update: " + uriMatcher.match(uri));
//        switch (uriMatcher.match(uri)){
//            case 1:
//                count = DB.update(TABLE_NAME, values, selection, selectionArgs);
//                break;
//            case 2:
//
//                break;
//            case 3:
//                count = DB.update(STUDENTS_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) +
//                        (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
//                break;
//
//            default:
//                throw new IllegalArgumentException("Unknown URI " + uri );
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
//        return count;
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
