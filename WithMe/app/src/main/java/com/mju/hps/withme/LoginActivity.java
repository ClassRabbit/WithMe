package com.mju.hps.withme;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.database.DatabaseSchema;
import com.mju.hps.withme.jni.WithMeJni;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final int MSG_LOGIN_SUCCESS = 1;
    private static final int MSG_LOGIN_FAIL = 2;
    private static final int MSG_LOGIN_ERROR = 3;

    EditText mailEditText;
    EditText passwordEditText;
    Button loginButton;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupUI(findViewById(R.id.activity_login));

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str;
                switch (msg.what) {
                    case MSG_LOGIN_SUCCESS:     // 성공
                        str = (String)msg.obj;
                        break;
                    case MSG_LOGIN_FAIL:     // 실패
                        str = (String)msg.obj;
                        mailEditText.setText(null);
                        passwordEditText.setText(null);
                        break;
                    case MSG_LOGIN_ERROR:     // 에러
                        str = (String)msg.obj;
                        break;
                }
                loginButton.setClickable(true);
            }
        };

        mailEditText = (EditText)findViewById(R.id.login_input_mail);
        passwordEditText = (EditText)findViewById(R.id.login_input_password);
        loginButton = (Button)findViewById(R.id.login_button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(), "메일과 비밀번호를 넣어주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginButton.setClickable(false);
                login(view);
                mailEditText.clearFocus();
                passwordEditText.clearFocus();
            }
        });

        TextView textView = (TextView)findViewById(R.id.textText);
        WithMeJni jni = new WithMeJni();
        textView.setText("" + jni.isSamePassword("test", "test"));

    }

    public void login(View view) {
        final String json = "{" +
                    "\"mail\" : \"" + mailEditText.getText().toString() + "\", " +
                    "\"password\" : \"" + passwordEditText.getText().toString() + "\"," +
                    "\"token\" : \"" + FirebaseInstanceId.getInstance().getToken() + "\"" +
                "}";
        final Activity activity = this;
        new Thread() {
            public void run() {
                String response = ServerManager.getInstance().post(Constants.SERVER_URL + "/user/login", json);
                if(response == null){
                    Log.e("login", "서버 에러");
                    handler.sendMessage(Message.obtain(handler, MSG_LOGIN_ERROR, ""));
                    activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.loginError"));
                    return;
                }
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
                        handler.sendMessage(Message.obtain(handler, MSG_LOGIN_SUCCESS, ""));
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.loginSuccess"));
                    }
                    else if(obj.getString("result").equals("fail")) {
                        Log.e("login", "로그인 실패");
                        handler.sendMessage(Message.obtain(handler, MSG_LOGIN_FAIL, ""));
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.loginFail"));
                    }
                    else {
                        Log.e("login", "서버 에러");
                        handler.sendMessage(Message.obtain(handler, MSG_LOGIN_ERROR, ""));
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.loginError"));
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


    //
    // 키보드 숨김 함수
    //
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

}
