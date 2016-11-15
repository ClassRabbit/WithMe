package com.mju.hps.withme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.server.ServerManager;

public class MainActivity extends AppCompatActivity {
    ServerManager serverManager;
    Button button;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
        startService(intent);
        Intent intent2 = new Intent(this, MyFirebaseMessagingService.class);
        startService(intent2);
        token = FirebaseInstanceId.getInstance().getToken();
        Log.e("token", "token: " + token);


        serverManager = ServerManager.getInstance();

    }

    public void testAction(View view) {
        new Thread() {
            public void run() {
//                String result = serverManager.get(Constants.SERVER_URL + "/test");
//                Log.e("result", result);
                String result = serverManager.post(Constants.SERVER_URL + "/fcm", token);
            }
        }.start();
    }
}
