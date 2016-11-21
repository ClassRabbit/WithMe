package com.mju.hps.withme.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

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

//    public void createUser(final Activity activity, final String mail, final String password, final String token,
//                           final String name, final String birth, final int phone, final String gender){
//        final String json = "{" +
//                "\"mail\" : \""  + mail + "\", " +
//                "\"password\" : \""  + password + "\", " +
//                "\"token\" : \""  + token + "\", " +
//                "\"name\" : \""  + name + "\", " +
//                "\"birth\" : \""  + birth + "\", " +
//                "\"phone\" : \""  + phone + "\", " +
//                "\"gender\" : \""  + gender + "\"" +
//            "}";
//
//        new Thread() {
//            public void run() {                                                       //서버 내용 수정
//                String result = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
//                Log.d("createUserResult", result);
//                try {
//                    JSONObject obj = new JSONObject(result);
//                    result = obj.getString("result");
//                    if(result.equals("fail")){
////                        User.getInstance().setMail("");
////                        User.getInstance().setPassword("");
////                        User.getInstance().setName("");
////                        User.getInstance().setBirth("");
////                        User.getInstance().setPhone(0);
////                        User.getInstance().setGender("");
//                        Log.e("createUser", "회원가입실패");
//                        activity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.createUserFail"));
//                    }
//                    else {
////                        User.getInstance().setId(result);
////                        ContentValues values = getContentValues(User.getInstance());
////                        database.update(UserTable.NAME, values, "flag = ?", new String[]{"1"});
//                        Log.e("createUser", "회원가입성공");
//                        activity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.createUserSuccess"));
//                    }
//
//                } catch (Throwable t) {
//                    Log.e("createUser", t.toString());
//                }
//            }
//        }.start();
//    }

    public void loginUser(){
        ContentValues values = getContentValues(User.getInstance());
        Log.i("loginUser", "database Login");
        database.update(UserTable.NAME, values, "flag = ?", new String[]{"1"});

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

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public Bitmap getBitmapFromURL(String src) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(src);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally{
            if(connection!=null)connection.disconnect();
        }
    }

}
