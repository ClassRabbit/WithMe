package com.mju.hps.withme;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.service.FcmInstanceIdService;
import com.mju.hps.withme.service.FcmMessagingService;

public class IntroActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //
        // init 필수내용
        //
        Intent intent = new Intent(this, FcmMessagingService.class);
        startService(intent);
        Intent intent2 = new Intent(this, FcmInstanceIdService.class);
        startService(intent2);
        DatabaseLab.setInstance(this);
        User.setInstance();
        Log.i("id", User.getInstance().getId());
        if(User.getInstance().getId().equals("")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                    finish();
                }
            }, 2000);
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
            }, 2000);
        }


    }
}
