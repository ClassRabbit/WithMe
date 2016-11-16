package com.mju.hps.withme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mju.hps.withme.CursorWrapper.UserCursorWrapper;
import com.mju.hps.withme.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.mju.hps.withme.database.DatabaseSchema.*;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class DatabaseLab {
    private static DatabaseLab databaseLab;
    private Context context;
    private SQLiteDatabase database;

    private DatabaseLab (Context context){
        this.context = context.getApplicationContext();
        database = new DatabaseHelper(context).getWritableDatabase();
    }

    public static void setInstance(Context context){
        if(databaseLab == null){
            databaseLab = new DatabaseLab(context);
        }
    }

    public static DatabaseLab getInstance(){
        return databaseLab;
    }

//    public void addUser(User user){
//        ContentValues values = getContentValues(user);
//        database.insert(UserTable.NAME, null, values);
//    }

    public void updateUser(User user){          // 미구현
//        String uuidString = phone.getId().toString();
//        ContentValues values = getContentValues(phone);
//        mDatabase.update(PhoneTable.NAME, values, PhoneTable.Cols.UUID + " = ?", new String[]{uudiString});
    }

    private void queryUser(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                UserTable.NAME,
                null, // 테이블 열이 null 인 경우, 즉 모든 열을 의미
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        UserCursorWrapper userCursorWrapper = new UserCursorWrapper(cursor);
        try{
            userCursorWrapper.moveToFirst();
            userCursorWrapper.settingUser();
        }
        finally {
            cursor.close();
        }
        userCursorWrapper.settingUser();
    }

    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.Cols.ID, user.getId());
        values.put(UserTable.Cols.MAIL, user.getMail());
        values.put(UserTable.Cols.PASSWORD, user.getPassword());
        values.put(UserTable.Cols.TOKEN, user.getToken());
        return values;
    }


}
