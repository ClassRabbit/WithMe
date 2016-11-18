package com.mju.hps.withme.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.mju.hps.withme.MainActivity;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

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

    public void createUser(Activity activity){
        final Activity finalActivity = activity;
        final String json = "{" +
                "\"mail\" : \""  + User.getInstance().getMail() + "\", " +
                "\"password\" : \""  + User.getInstance().getPassword() + "\", " +
                "\"token\" : \""  + User.getInstance().getToken() + "\", " +
                "\"name\" : \""  + User.getInstance().getName() + "\", " +
                "\"birth\" : \""  + User.getInstance().getBirth() + "\", " +
                "\"phone\" : \""  + User.getInstance().getPhone() + "\", " +
                "\"gender\" : \""  + User.getInstance().getGender() + "\"" +
            "}";

        new Thread() {
            public void run() {                                                       //서버 내용 수정
                String result = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
                Log.d("createUserResult", result);
                try {
                    JSONObject obj = new JSONObject(result);
                    result = obj.getString("result");
                    if(result.equals("fail")){
                        User.getInstance().setMail("");
                        User.getInstance().setPassword("");
                        User.getInstance().setName("");
                        User.getInstance().setBirth("");
                        User.getInstance().setPhone(0);
                        User.getInstance().setGender("");
                        Log.e("createUser", "회원가입실패");
                        finalActivity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.createUserFail"));
                    }
                    else {
                        User.getInstance().setId(result);
                        ContentValues values = getContentValues(User.getInstance());
                        database.update(UserTable.NAME, values, "flag = ?", new String[]{"1"});
                        Log.e("createUser", "회원가입성공");
                        finalActivity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.createUserSuccess"));
                    }

                } catch (Throwable t) {
                    Log.e("createUser", t.toString());
                }
            }
        }.start();
    }

    public void updateUser(){                                          //현재유저상태로 DB덮어씌움
        ContentValues values = getContentValues(User.getInstance());
        database.update(UserTable.NAME, values, "flag = ?", new String[]{"1"});
        final String json = "{" +
//                    "\"id\" : \"" + User.getInstance().getId() + "\", " +
                    "\"mail\" : \""  + User.getInstance().getMail() + "\", " +
                    "\"password\" : \""  + User.getInstance().getPassword() + "\", " +
                    "\"token\" : \""  + User.getInstance().getToken() + "\", " +
                    "\"name\" : \""  + User.getInstance().getName() + "\", " +
                    "\"birth\" : \""  + User.getInstance().getBirth() + "\", " +
                    "\"phone\" : \""  + User.getInstance().getPhone() + "\", " +
                    "\"gender\" : \""  + User.getInstance().getGender() + "\"" +
                "\"}";
        new Thread() {
            public void run() {                                                       //서버 내용 수정
                String result = ServerManager.getInstance().put(Constants.SERVER_URL + "/user", json);
                Log.e("result", result);

            }
        }.start();
    }

    public void setUser(String whereClause, String[] whereArgs){
        Cursor cursor = database.query(
                UserTable.NAME,
                null, // 테이블 열이 null 인 경우, 즉 모든 열을 의미
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        try{
            cursor.moveToFirst();
            Log.e("corsor size", "" + cursor.getCount());
            User.getInstance().setId(cursor.getString(cursor.getColumnIndex(UserTable.Cols.ID)));
            User.getInstance().setMail(cursor.getString(cursor.getColumnIndex(UserTable.Cols.MAIL)));
            User.getInstance().setPassword(cursor.getString(cursor.getColumnIndex(UserTable.Cols.PASSWORD)));
            User.getInstance().setToken(cursor.getString(cursor.getColumnIndex(UserTable.Cols.TOKEN)));
            User.getInstance().setName(cursor.getString(cursor.getColumnIndex(UserTable.Cols.NAME)));
            User.getInstance().setBirth(cursor.getString(cursor.getColumnIndex(UserTable.Cols.BIRTH)));
            User.getInstance().setPhone(Integer.parseInt(cursor.getString(cursor.getColumnIndex(UserTable.Cols.PHONE))));
            User.getInstance().setGender(cursor.getString(cursor.getColumnIndex(UserTable.Cols.GENDER)));
        }
        finally {
            cursor.close();
        }
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
