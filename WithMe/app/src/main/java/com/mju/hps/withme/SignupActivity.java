package com.mju.hps.withme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mju.hps.withme.database.DatabaseLab;
import com.mju.hps.withme.model.User;

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
        mail = (EditText)findViewById(R.id.input_user_mail);
        password = (EditText)findViewById(R.id.input_user_pwd);
        name = (EditText)findViewById(R.id.input_user_name);
        birth = (EditText)findViewById(R.id.input_user_birth);
        phone = (EditText)findViewById(R.id.input_user_phone);
        gender = (EditText)findViewById(R.id.input_user_gender);
    }

    public void createUser(View view) {
        User.getInstance().setMail(mail.getText().toString());
        User.getInstance().setPassword(password.getText().toString());
        User.getInstance().setToken(FirebaseInstanceId.getInstance().getToken());
        User.getInstance().setName(name.getText().toString());
        User.getInstance().setBirth(birth.getText().toString());
        // null값 들어오면 죽음
        User.getInstance().setPhone(Integer.parseInt(phone.getText().toString()));
        User.getInstance().setGender(gender.getText().toString());
        DatabaseLab.getInstance().createUser(this);
    }
}
