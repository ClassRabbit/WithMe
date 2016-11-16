package com.mju.hps.withme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.mju.hps.withme.database.DatabaseSchema.*;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "withMe.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("db", "생성");
        db.execSQL(                                     //User테이블생성
            "create table " + UserTable.NAME + "(" +
                    "flag integer," +
                    UserTable.Cols.ID + " text, " +
                    UserTable.Cols.PASSWORD + " text, " +
                    UserTable.Cols.MAIL + " text, " +
                    UserTable.Cols.TOKEN + " text" +
                ")"
        );
        ContentValues values = new ContentValues();     //User 하나 넣기
        values.put("flag", "1");
        values.put(UserTable.Cols.ID, "");
        values.put(UserTable.Cols.MAIL, "");
        values.put(UserTable.Cols.PASSWORD, "");
        values.put(UserTable.Cols.TOKEN, "");
        db.insert(UserTable.NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
