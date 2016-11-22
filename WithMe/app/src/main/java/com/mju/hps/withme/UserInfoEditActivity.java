package com.mju.hps.withme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.mju.hps.withme.model.User;

public class UserInfoEditActivity extends AppCompatActivity {
    EditText email, name, birth, gender, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);

        email = (EditText)findViewById(R.id.user_info_edit_email);
        name = (EditText)findViewById(R.id.user_info_edit_name);
        birth = (EditText)findViewById(R.id.user_info_edit_birth);
        gender = (EditText)findViewById(R.id.user_info_edit_gender);
        phone = (EditText)findViewById(R.id.user_info_edit_phone);

    }

    @Override
    protected void onStart() {
        super.onStart();
        email.setText(User.getInstance().getMail());
        name.setText(User.getInstance().getName());
        birth.setText(User.getInstance().getBirth());
        gender.setText(User.getInstance().getGender());
        phone.setText("" + User.getInstance().getPhone());
    }
}
