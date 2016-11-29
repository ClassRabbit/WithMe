package com.mju.hps.withme.constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by KMC on 2016. 11. 15..
 */

public class Constants {
    public static final String SERVER_URL = "http://192.168.40.22:3000";          //민찬 연구실 맥미니
//    public static final String SERVER_URL = "http://192.168.0.2:3000"; //유태 기숙사
//    public static final String SERVER_URL = "http://192.168.0.7:3000"; //유태 기숙사
//    public static final String SERVER_URL = "http://192.168.123.105:3000";          //민찬 자취방
//    public static final String SERVER_URL = "http://192.168.1.125:3000"; //유태 레알집
//    192.168.0.7

    public static final int REQUEST_CODE_LOCATION_COARSE = 1;
    public static final int REQUEST_CODE_LOCATION_FINE = 2;
    public static final int REQUEST_CODE_PHOTO_PICKER = 3;

    public static final int PICK_FROM_CAMERA = 4;
    public static final int PICK_FROM_ALBUM = 5;
    public static final int CROP_FROM_CAMERA = 6;
//    public static enum EFileMenuItems{새로만들기, 열기, 저장, 다른이름으로저장, 종료};
}
