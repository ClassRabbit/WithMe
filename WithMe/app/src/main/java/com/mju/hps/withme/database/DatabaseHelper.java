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
        db.execSQL(                                     //User 테이블생성
                "create table " + UserTable.NAME + "(" +
                        "flag integer," +
                        UserTable.Cols.ID + " text, " +
                        UserTable.Cols.PASSWORD + " text, " +
                        UserTable.Cols.MAIL + " text, " +
                        UserTable.Cols.TOKEN + " text," +
                        UserTable.Cols.NAME + " text," +
                        UserTable.Cols.BIRTH + " text," +
                        UserTable.Cols.PHONE + " text," +
                        UserTable.Cols.GENDER + " text" +
                        ")"
        );
        ContentValues values = new ContentValues();     //User 하나 넣기
        values.put("flag", "1");
        values.put(UserTable.Cols.ID, "");
        values.put(UserTable.Cols.MAIL, "");
        values.put(UserTable.Cols.PASSWORD, "");
        values.put(UserTable.Cols.TOKEN, "");
        values.put(UserTable.Cols.NAME, "");
        values.put(UserTable.Cols.BIRTH, "");
        values.put(UserTable.Cols.PHONE, 0);
        values.put(UserTable.Cols.GENDER, "");
        db.insert(UserTable.NAME, null, values);

//        db.execSQL(                                     //Room 테이블생성
//                "create table " + RoomTable.NAME + "(" +
//                        "flag integer," +
//                        RoomTable.Cols.ID + " text " +
//                        ")"
//        );
//        values = new ContentValues();     //Room 하나 넣기
//        values.put("flag", "1");
//        values.put(UserTable.Cols.ID, "");
//        db.insert(RoomTable.NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
