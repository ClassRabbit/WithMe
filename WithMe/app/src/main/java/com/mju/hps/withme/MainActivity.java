package com.mju.hps.withme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.server.ServerManager;

public class MainActivity extends AppCompatActivity {
    ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serverManager = ServerManager.getInstance();
        new Thread() {
            public void run() {
                String result = serverManager.get(Constants.SERVER_URL + "/test");
                Log.e("result", result);
            }
        }.start();

    }
}
