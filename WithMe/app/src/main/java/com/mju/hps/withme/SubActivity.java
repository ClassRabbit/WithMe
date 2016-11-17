package com.mju.hps.withme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;

public class SubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Intent intent=new Intent(this.getIntent());
        String s=intent.getStringExtra("text");
        Log.e("text", s);
    }

    public void testCreateUser(View view) {
        User.getInstance().setMail("test14@gamil.com");
        User.getInstance().setPassword("1234");
        User.getInstance().setToken(FirebaseInstanceId.getInstance().getToken());
        DatabaseLab.getInstance().createUser(this);
    }
}
