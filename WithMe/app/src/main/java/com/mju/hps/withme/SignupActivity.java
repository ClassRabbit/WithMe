package com.mju.hps.withme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

import java.util.Date;

public class SignupActivity extends AppCompatActivity {
    EditText mail;
    EditText password;
    EditText name;
    EditText birth;
    EditText phone;
    EditText gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        mail = (EditText)findViewById(R.id.input_user_mail);
//        password = (EditText)findViewById(R.id.input_user_pwd);
//        name = (EditText)findViewById(R.id.input_user_name);
        birth = (EditText)findViewById(R.id.signup_input_birth);
//        phone = (EditText)findViewById(R.id.input_user_phone);
//        gender = (EditText)findViewById(R.id.input_user_gender);
    }

    public void createUser(View view) {
        //
        //  입력 체크 INJ할것
        //
        final String json = "{" +
            "\"mail\" : \""  + mail.getText().toString() + "\", " +
            "\"password\" : \""  + password.getText().toString() + "\", " +
            "\"token\" : \""  + FirebaseInstanceId.getInstance().getToken() + "\", " +
            "\"name\" : \""  + name.getText().toString() + "\", " +
            "\"birth\" : \""  + birth.getText().toString() + "\", " +
            "\"phone\" : \""  + phone.getText().toString() + "\", " +
            "\"gender\" : \""  + gender.getText().toString() + "\"" +
        "}";
        final Activity activity = this;
        new Thread() {
            public void run() {                                                       //서버 내용 수정
                String responseStr = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
                Log.d("createUserResult", responseStr);
                try {
                    JSONObject response = new JSONObject(responseStr);
                    String result = response.getString("result");
                    if(result.equals("fail")){
                        Log.e("createUser", "회원가입실패");
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserFail"));
                    }
                    else {
                        Log.e("createUser", "회원가입성공");
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserSuccess"));
                    }

                } catch (Throwable t) {
                    Log.e("createUser", t.toString());
                }
            }
        }.start();
    }

    public void onBirthClicked (View v){
        new DatePickerDialog(this, dateSetListener, 2016, 12, 25).show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
//            String msg = String.format("%d / %d / %d", year,monthOfYear+1, dayOfMonth);
//            Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_SHORT).show();
            birth.setText("" + year + "." + monthOfYear+1 +"." + dayOfMonth);
        }
    };


//    public void hideKeyboard(){
//        View currentFocus = SignupActivity.this.getCurrentFocus();    // Change the name according to your activity's name.
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(SignupActivity.this.INPUT_METHOD_SERVICE);
//        if(currentFocus != null){
//            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(),0);
//            currentFocus.clearFocus();
//        }
//    }


}
