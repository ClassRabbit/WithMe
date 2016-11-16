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
        Intent intent = new Intent(this, FcmMessagingService.class);
        startService(intent);
        Intent intent2 = new Intent(this, FcmInstanceIdService.class);
        startService(intent2);



        serverManager = ServerManager.getInstance();

    }

    public void testAction(View view) {
        token = "{est:'" + FirebaseInstanceId.getInstance().getToken() + "'}";
        Log.e("token", "token: " + token);
        new Thread() {
            public void run() {
//                String result = serverManager.get(Constants.SERVER_URL + "/fcm");
                String result = serverManager.post(Constants.SERVER_URL + "/fcm", token);
                Log.e("result", result);
            }
        }.start();
    }

}
