package com.mju.hps.withme;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.Room;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.service.FcmInstanceIdService;
import com.mju.hps.withme.service.FcmMessagingService;

public class IntroActivity extends Activity {

    Button introButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introButton =(Button)findViewById(R.id.intro_button);
        int permissionCnt = 0;

        //
        // init 필수내용
        //
        Intent intent = new Intent(this, FcmMessagingService.class);
        startService(intent);
        Intent intent2 = new Intent(this, FcmInstanceIdService.class);
        startService(intent2);
        DatabaseLab.setInstance(this);
        User.setInstance();
//        Room.setInstance();
        Log.i("id", User.getInstance().getId());
        //
        //
        //


        //permition add
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                Log.e("permission", permission);
                int result = PermissionChecker.checkSelfPermission(this, permission);
                if (result == PermissionChecker.PERMISSION_GRANTED){
                    permissionCnt++;
                }
                else {
                    if(permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                        ActivityCompat.requestPermissions(this ,permissions , Constants.REQUEST_CODE_LOCATION_COARSE);
                    }
                    else if(permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                        ActivityCompat.requestPermissions(this ,permissions, Constants.REQUEST_CODE_LOCATION_FINE);
                    }
                    else if(permission.equals(Manifest.permission.CAMERA)){
                        ActivityCompat.requestPermissions(this ,permissions, Constants.PICK_FROM_CAMERA);
                    }
                    else if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(this ,permissions, Constants.PICK_FROM_CAMERA);
                    }
                    else if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(this ,permissions, Constants.PICK_FROM_CAMERA);
                    }
                    else {
                        ActivityCompat.requestPermissions(this, permissions, 1);
                    }
                }
            }
        }

        if(permissionCnt == permissions.length) {
            introButton.setVisibility(View.INVISIBLE);
            if(User.getInstance().getId().equals("")) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                        finish();
                    }
                }, 1000);
            }
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                        startActivity(intent);
                        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                        finish();
                    }
                }, 1000);
            }
        }

//        //permission.
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_CODE_LOCATION_COARSE);
//        }
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION_FINE);
//        }
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.CAMERA}, Constants.PICK_FROM_CAMERA);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PICK_FROM_CAMERA);
//        }
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.PICK_FROM_CAMERA);
//        }
    }
    void introButtonAct(View view){
        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
        startActivity(intent);
        // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
        finish();

    }
}
