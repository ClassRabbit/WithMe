package com.mju.hps.withme.CursorWrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.mju.hps.withme.database.DatabaseSchema;
import com.mju.hps.withme.database.DatabaseSchema.UserTable;
import com.mju.hps.withme.model.User;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public void settingUser(){
        String id = getString(getColumnIndex(UserTable.Cols.ID));
        String mail = getString(getColumnIndex(UserTable.Cols.MAIL));
        String password = getString(getColumnIndex(UserTable.Cols.PASSWORD));
        String token = getString(getColumnIndex(UserTable.Cols.TOKEN));
        User.getInstance().setId(id);
        User.getInstance().setMail(mail);
        User.getInstance().setPassword(password);
        User.getInstance().setToken(token);
//        return new User(id, mail, password, token);
    }
}
