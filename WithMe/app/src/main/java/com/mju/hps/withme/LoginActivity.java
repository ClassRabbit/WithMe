package com.mju.hps.withme;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.database.DatabaseSchema;
import com.mju.hps.withme.jni.WithMeJni;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText mail;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mail = (EditText)findViewById(R.id.input_mail);
        password = (EditText)findViewById(R.id.input_password);

        TextView textView = (TextView)findViewById(R.id.textText);
        WithMeJni jni = new WithMeJni();
        textView.setText("" + jni.isSamePassword("test", "test"));
    }

    public void login(View view) {
        final String json = "{" +
                    "\"mail\" : \"" + mail.getText().toString() + "\", " +
                    "\"password\" : \"" + password.getText().toString() + "\"" +
                "}";
        final Activity activity = this;
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
                Log.e("loginResponse", response);
                try{
                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("id") != null){
                        User.getInstance().setId(obj.getString("id"));
                        User.getInstance().setMail(obj.getString("mail"));
                        User.getInstance().setPassword(obj.getString("password"));
                        User.getInstance().setToken(obj.getString("token"));
                        User.getInstance().setName(obj.getString("name"));
                        User.getInstance().setBirth(obj.getString("birth"));
                        User.getInstance().setPhone(Integer.parseInt(obj.getString("phone")));
                        User.getInstance().setGender(obj.getString("gender"));
                        DatabaseLab.getInstance().loginUser();
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.loginSuccess"));      //리시버 만들것
                    }
                    else {
                        //리시버 만들어서 던지기
                        //창에 입력란도 비울것
                    }
                }
                catch (Exception e) {
                    Log.e("login", e.toString());
                }

            }
        }.start();
    }

    public void sign_up(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
