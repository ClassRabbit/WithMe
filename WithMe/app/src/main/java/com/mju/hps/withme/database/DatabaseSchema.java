package com.mju.hps.withme.database;

/**
 * Created by KMC on 2016. 11. 16..
 */

public class DatabaseSchema {
    public static final class UserTable {
        public static final String NAME = "User";
        public static final class Cols {
            //        public static final String UUID = "uuid";
            public static final String ID = "id";
            public static final String MAIL = "mail";
            public static final String PASSWORD = "password";
            public static final String TOKEN = "token";
            public static final String NAME = "name";
            public static final String BIRTH = "birth";
            public static final String PHONE = "phone";
            public static final String GENDER = "gender";
        }
    }
}
