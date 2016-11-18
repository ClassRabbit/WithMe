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
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/user/login", json);
                Log.e("loginResponse", response);
                try{
                    JSONObject obj = new JSONObject(response);
                    if(obj.getString("result").equals("success")){
                        Log.e("login", "로그인 성공");
                        JSONObject user = new JSONObject(obj.getString("user"));
                        User.getInstance().setId(user.getString("id"));
                        User.getInstance().setMail(user.getString("mail"));
                        User.getInstance().setPassword(user.getString("password"));
                        User.getInstance().setToken(user.getString("token"));
                        User.getInstance().setName(user.getString("name"));
                        User.getInstance().setBirth(user.getString("birth"));
                        User.getInstance().setPhone(Integer.parseInt(user.getString("phone")));
                        User.getInstance().setGender(user.getString("gender"));
                        DatabaseLab.getInstance().loginUser();
//                        activity.sendBroadcast(new Intent("com.mju.hps.withme.sendreciver.loginSuccess"));      //리시버 만들것
                    }
                    else if(obj.getString("result").equals("fail")) {
                        Log.e("login", "로그인 실패");
                        //리시버 만들어서 던지기
                        //창에 입력란도 비울것
                    }
                    else {
                        Log.e("login", "서버 에러");
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
