package com.mju.hps.withme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    }

    public void testAction(View view) {
        token = "{\"token\":\"" + FirebaseInstanceId.getInstance().getToken() + "\"}";
        Log.e("token", "token: " + token);
        new Thread() {
            public void run() {
                String result = serverManager.post(Constants.SERVER_URL + "/fcm", token);
                Log.e("result", result);
            }
        }.start();
    }

    public void moveAction(View view) {
        Intent intent2=new Intent(this, SubActivity.class);
        intent2.putExtra("text","data");
        this.startActivity(intent2);

    }



}
