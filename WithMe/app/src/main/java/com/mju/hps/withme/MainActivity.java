package com.mju.hps.withme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;
import com.mju.hps.withme.service.FcmInstanceIdService;
import com.mju.hps.withme.service.FcmMessagingService;

public class MainActivity extends AppCompatActivity {
    ServerManager serverManager;
    Button button;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        //
        // init 필수내용
        //
        Intent intent = new Intent(this, FcmMessagingService.class);
        startService(intent);
        Intent intent2 = new Intent(this, FcmInstanceIdService.class);
        startService(intent2);
        DatabaseLab.setInstance(this);
        User.setInstance();
        serverManager = ServerManager.getInstance();
        //
        //
        //

    }

    public void testAction(View view) {
        token = "{\"token\":\"" + FirebaseInstanceId.getInstance().getToken() + "\"}";
        Log.e("token", "token: " + token);
        new Thread() {
            public void run() {
//                String result = serverManager.get(Constants.SERVER_URL + "/fcm");
                String result = serverManager.post(Constants.SERVER_URL + "/fcm", token);
                Log.e("result", result);
            }
        }.start();
    }

    public void testCreateUser(View view) {
        User.getInstance().setMail("test4@gamil.com");
        User.getInstance().setPassword("1234");
        User.getInstance().setToken(FirebaseInstanceId.getInstance().getToken());
        DatabaseLab.getInstance().createUser(this);
    }

}
