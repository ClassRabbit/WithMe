package com.mju.hps.withme;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

        setupUI(findViewById(R.id.activity_user_info_edit));

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
                    hideSoftKeyboard(UserInfoEditActivity.this);
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
