package com.mju.hps.withme;

import android.content.Intent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.mju.hps.withme.model.User;
import com.mju.hps.withme.server.ServerManager;

public class UserInfoActivity extends AppCompatActivity {

    TextView email, name, birth, gender, phone;

    ImageView profileImage;
//    http://localhost:3000/images/user/yt@.png

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        email = (TextView)findViewById(R.id.user_info_email);
        name = (TextView)findViewById(R.id.user_info_name);
        birth = (TextView)findViewById(R.id.user_info_birth);
        gender = (TextView)findViewById(R.id.user_info_gender);
        phone = (TextView)findViewById(R.id.user_info_phone);

        profileImage = (ImageView) findViewById(R.id.user_info_profile);

        ServerManager.getInstance().getUserProfileImage(User.getInstance().getMail(), profileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_info_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.user_info_edit) {
            Intent intent = new Intent(this, UserInfoEditActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
