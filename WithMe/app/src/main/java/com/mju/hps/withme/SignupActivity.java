package com.mju.hps.withme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.constants.Constants;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.jni.WithMeJni;
import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    EditText mail;
    EditText password;
    EditText passwordConfirm;
    EditText name;
    EditText birth;
    EditText phone;
    RadioGroup genderGroup;


    protected InputFilter filterPhone = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mail = (EditText)findViewById(R.id.signup_input_mail);
        password = (EditText)findViewById(R.id.signup_input_password);
        passwordConfirm = (EditText)findViewById(R.id.signup_input_password_confirm);
        name = (EditText)findViewById(R.id.signup_input_name);
        birth = (EditText)findViewById(R.id.signup_input_birth);
        phone = (EditText)findViewById(R.id.signup_input_phone);
        genderGroup = (RadioGroup)findViewById(R.id.signup_group_gender);

        phone.setFilters(new InputFilter[] {filterPhone});
    }

    public void createUser(View view) {
        WithMeJni jni = new WithMeJni();
        if(mail.getText().toString().equals("")){
            Toast.makeText(this, "메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().toString().equals("")){
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(passwordConfirm.getText().toString().equals("")){
            Toast.makeText(this, "비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(name.getText().toString().equals("")){
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(birth.getText().toString().equals("")){
            Toast.makeText(this, "생년월일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(jni.isValidMail(mail.getText().toString()) == 0){
            Toast.makeText(this, "메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(jni.isValidPhone(phone.getText().toString()) == 0){
            Toast.makeText(this, "핸드폰 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(jni.isSamePassword(password.getText().toString(), passwordConfirm.getText().toString()) == 0){
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton gender = (RadioButton)findViewById(genderGroup.getCheckedRadioButtonId());
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
            public void run() {
                String responseStr = ServerManager.getInstance().post(Constants.SERVER_URL + "/user", json);
                Log.d("createUserResult", responseStr);
                try {
                    JSONObject response = new JSONObject(responseStr);
                    String result = response.getString("result");
                    if(result.equals("fail")){
                        activity.sendBroadcast(new Intent("com.mju.hps.withme.reciver.createUserFail"));
                    }
                    else {
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
